FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += " \
    file://0001-Update-for-rpi4-on-CompuLab-boards.patch \
    file://0002-tools-fw_env.config-Enable-VFAT-uboot.env-file.patch \
    file://compulab.cfg \
"

do_install:append () {
	install -d ${D}/etc/
	install -m 0644 ${S}/tools/env/fw_env.config  ${D}/etc/fw_env.config
}

UBOOT_INITIAL_ENV = "u-boot-initial-env"
FILES:${PN}-env += "/etc/*"
