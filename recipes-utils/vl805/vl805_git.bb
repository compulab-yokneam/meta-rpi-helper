LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

SRC_URI = "git://github.com/compulab-yokneam/vl805.git;protocol=https;branch=master"
SRC_URI:append = " file://vl805.sh "

# Modify these as desired
PV = "1.0+git"
SRCREV = "${AUTOREV}"

inherit  update-rc.d

INITSCRIPT_NAME = "vl805.sh"
INITSCRIPT_PARAMS = "start 50 5 ."

do_configure () {
	oe_runmake config
}

do_compile () {
	oe_runmake
}

do_install () {
	install -d ${D}/opt/vl805
	install -d ${D}/opt/vl805/firmware
	install -m 0755 ${S}/vl805 ${D}/opt/vl805/
	install -m 0755 ${S}/scripts/vl805_flash.sh ${D}/opt/vl805/
	install -m 0644 ${S}/SpiFlash.ini ${D}/opt/vl805/
	install -m 0644 ${S}/firmware/fw00013705_020000.bin ${D}/opt/vl805/firmware/
	#
	install -d ${D}/${sysconfdir}/init.d
	install -m 0755 ${UNPACKDIR}/vl805.sh ${D}/${sysconfdir}/init.d/
}

FILES:${PN} = " \
	/opt \
	/etc \
"

PACKAGE_ARCH = "${MACHINE_ARCH}"

DEPENDS = "zlib"

RDEPENDS:${PN} = "bash"
