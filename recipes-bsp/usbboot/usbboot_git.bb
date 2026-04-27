LICENSE = "CLOSED"
LIC_FILES_CHKSUM = ""

LLICENSE = "Apache-2.0 & Unknown"
LLIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e \
                    file://rpi-eeprom/LICENSE;md5=a6c5149578a16272119f3f9c13d6549b \
                    file://win32/LICENSE.txt;md5=6e2c54ab7708d1bdc7079474442fc43b"

SRC_URI = "gitsm://github.com/raspberrypi/usbboot.git;protocol=https;branch=master"
SRC_URI:append = " \
    file://msd/cmdline.txt \
    file://msd/config.txt \
    file://uboot/config.txt \
"

# Modify these as desired
PV = "1.0+git"
SRCREV = "${AUTOREV}"

inherit deploy nopackages native pkgconfig

DEPENDS += " \
    libusb-native \
"

do_configure () {
	:
}

do_compile () {
	oe_runmake 'CC_FOR_BUILD=${BUILD_CC}' 'CFLAGS= -static'
}

RPI_IMAGE = "core-image-full-cmdline"

make_boot_img() {

    BOOTDD_VOLUME_ID="rpi"
    INDIR=${INDIR}
    SDIMG=boot.img
    PART=part.img

    SIZE_MB=$(du -sBM ${INDIR} | awk -F"M" '$0=$1')
    SIZE_MB=$(echo "( $SIZE_MB + 15 ) / 8 * 8" | bc)
    dd if=/dev/zero of=${SDIMG}  bs=1M count=${SIZE_MB}
    SIZE_B=$(echo "${SIZE_MB} * 2048 - 15" | bc)

    parted -s ${SDIMG} mklabel msdos
    parted -s ${SDIMG} unit s mkpart primary fat32 1 ${SIZE_B}
    parted -s ${SDIMG} set 1 boot on
    parted -s ${SDIMG} print

    BOOT_BLOCKS=$(LC_ALL=C parted -s ${SDIMG} unit b print | awk '/ 1 / { print substr($4, 1, length($4 -1)) / 512 /2 }')
    mkfs.vfat -F32 -n "${BOOTDD_VOLUME_ID}" -S 512 -C ${PART} ${BOOT_BLOCKS}
    mcopy -v -i ${PART} -s ${INDIR}/* ::/
    dd if=${PART}  of=${SDIMG} conv=notrunc seek=1 bs=512
    mv boot.img ${DEPLOY_DIR_IMAGE}/${BOOTIMAGE_FILE}

    rm -rf ${PART} ${SDIMG}
}

UBOOT_DIR = "rpiboot.uboot.in"
do_ubootdir() {
    PDD=${DEPLOY_DIR_IMAGE}/${UBOOT_DIR}

    install -d ${PDD}/overlays
    tar -C ${DEPLOY_DIR_IMAGE}/bootfiles -cf - . | tar -C ${PDD} -xf -
    cp -L ${DEPLOY_DIR_IMAGE}/Image ${PDD}/kernel8.img
    cp -L ${DEPLOY_DIR_IMAGE}/u-boot.bin ${PDD}/u-boot.bin
    install -m 0644 ${UNPACKDIR}/uboot/config.txt  ${PDD}/config.txt
    cd ${DEPLOY_DIR_IMAGE}

    for _file in *.dtb;do
        cp -vL $(readlink -e ${_file}) ${PDD}/
    done

    for _file in *.dtbo;do
        cp -vL $(readlink -e ${_file}) ${PDD}/overlays
    done

    cd -
}
do_ubootdir[depends] += "virtual/bootloader:do_deploy rpi-bootfiles:do_deploy"
addtask ubootdir after do_bootimage before do_deploy

BOOTIMAGE_DIR = "boot.img.in"
BOOTIMAGE_FILE = "boot.img"
do_bootimage() {
    PDD=${DEPLOY_DIR_IMAGE}/${BOOTIMAGE_DIR}

    rootfs_image=$(readlink -e ${DEPLOY_DIR_IMAGE}/${RPI_IMAGE}-${MACHINE}.rootfs.ext3)
    rootfs_image_size=$(stat --print=%s ${rootfs_image})
    rootfs_image_size=$(echo "(${rootfs_image_size} + 1023) / 1024" | bc)
    install -d ${PDD}/overlays
    tar -C ${DEPLOY_DIR_IMAGE}/bootfiles -cf - . | tar -C ${PDD} -xf -
    cp -L ${DEPLOY_DIR_IMAGE}/Image ${PDD}/kernel8.img
    zstd -9c ${rootfs_image} > ${PDD}/rootfs.ext3.zst

    cd ${DEPLOY_DIR_IMAGE}

    for _file in *.dtb;do
        cp -vL $(readlink -e ${_file}) ${PDD}/
    done

    for _file in *.dtbo;do
        cp -vL $(readlink -e ${_file}) ${PDD}/overlays
    done

    cd -

    install -m 0644 ${UNPACKDIR}/msd/config.txt  ${PDD}/config.txt
    install -m 0644 ${UNPACKDIR}/msd/cmdline.txt ${PDD}/cmdline.txt
    sed -i "s/@@RAMDISK_SIZE_KB@@/${rootfs_image_size}/g" ${PDD}/cmdline.txt

    INDIR=${DEPLOY_DIR_IMAGE}/${BOOTIMAGE_DIR} make_boot_img

    rm -rf ${PDD}
}
do_bootimage[depends] += "${RPI_IMAGE}:do_image rpi-bootfiles:do_deploy"
addtask bootimage after do_install before do_deploy

do_install () {
	oe_runmake install 'DESTDIR=${D}'
}

do_deploy:append() {
    cp -a ${DESTDIR}/usr/share/rpiboot/mass-storage-gadget64 ${DESTDIR}/usr/share/rpiboot/ramdisk-boot
    mv ${DEPLOY_DIR_IMAGE}/${BOOTIMAGE_FILE}  ${DESTDIR}/usr/share/rpiboot/ramdisk-boot/boot.img
    mv ${DEPLOY_DIR_IMAGE}/${UBOOT_DIR}  ${DESTDIR}/usr/share/rpiboot/uboot-boot
}

do_deploy() {
    install -d ${DEPLOYDIR}/${PN}
    DESTDIR=${DEPLOYDIR}/${PN}
    oe_runmake install DESTDIR=${DESTDIR}
}
addtask deploy after do_install before do_tarball

do_tarball() {
    tar -C ${DEPLOY_DIR_IMAGE} -cjvf ${DEPLOY_DIR_IMAGE}/${PN}.tar.bz2 ${PN}
}
addtask tarball after do_deploy
