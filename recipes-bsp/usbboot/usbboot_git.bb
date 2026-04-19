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

do_install:append() {
    PDD=${DEPLOY_DIR_IMAGE}/boot.img.in

    rootfs_image=$(readlink -e ${DEPLOY_DIR_IMAGE}/${RPI_IMAGE}-${MACHINE}.rootfs.ext3)
    rootfs_image_size=$(stat --print=%s ${rootfs_image})
    rootfs_image_size=$(echo "(${rootfs_image_size} + 1023) / 1024" | bc)
    install -d ${PDD}
    tar -C ${DEPLOY_DIR_IMAGE}/bootfiles -cf - . | tar -C ${PDD} -xf -
    cp -L ${DEPLOY_DIR_IMAGE}/Image ${PDD}/kernel8.img
    zstd -9c ${rootfs_image} > ${PDD}/rootfs.ext3.zst
    for _file in ${RPI_KERNEL_DEVICETREE};do
        cp -L ${DEPLOY_DIR_IMAGE}/$(basename ${_file}) ${PDD}/$(basename ${_file})
    done
    install -m 0644 ${UNPACKDIR}/msd/config.txt  ${PDD}/config.txt
    install -m 0644 ${UNPACKDIR}/msd/cmdline.txt ${PDD}/cmdline.txt
    sed -i "s/@@RAMDISK_SIZE_KB@@/${rootfs_image_size}/g" ${PDD}/cmdline.txt
}

do_install () {
	oe_runmake install 'DESTDIR=${D}'
}
do_install[depends] += "${RPI_IMAGE}:do_image rpi-bootfiles:do_deploy"

do_deploy() {
    install -d ${DEPLOYDIR}/${PN}
    oe_runmake install 'DESTDIR=${DEPLOYDIR}/${PN}'
}

addtask deploy after do_install
