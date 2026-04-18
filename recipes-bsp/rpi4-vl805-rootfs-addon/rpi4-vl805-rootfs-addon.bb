LICENSE = "CLOSED"
LIC_FILES_CHKSUM = ""

SRC_URI = "file://rpi4-vl805-rootfs-addon.tar.bz2"

do_configure () {
	:
}

do_compile () {
	:
}

do_install () {
	tar -C ${UNPACKDIR} -cf - opt etc  | tar -C ${D} -xf -
	chown -R 0:0 ${D}
}

FILES:${PN} = "etc/* opt/*"
RDEPENDS:${PN} = "bash zlib"
