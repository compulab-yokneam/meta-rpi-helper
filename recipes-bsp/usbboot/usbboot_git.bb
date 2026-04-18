LICENSE = "CLOSED"
LIC_FILES_CHKSUM = ""

LLICENSE = "Apache-2.0 & Unknown"
LLIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e \
                    file://rpi-eeprom/LICENSE;md5=a6c5149578a16272119f3f9c13d6549b \
                    file://win32/LICENSE.txt;md5=6e2c54ab7708d1bdc7079474442fc43b"

SRC_URI = "gitsm://github.com/raspberrypi/usbboot.git;protocol=https;branch=master"

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

do_install () {
	oe_runmake install 'DESTDIR=${D}'
}

do_deploy() {
    install -d ${DEPLOYDIR}/${PN}
    oe_runmake install 'DESTDIR=${DEPLOYDIR}/${PN}'
}

addtask deploy after do_install
