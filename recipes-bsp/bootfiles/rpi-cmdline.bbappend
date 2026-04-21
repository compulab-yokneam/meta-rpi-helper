FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " \
    file://cmdline.in \
"

do_deploy:append() {
    cat ${UNPACKDIR}/cmdline.in > ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/cmdline.txt
}
