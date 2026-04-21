FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " \
    file://config.in \
"

do_deploy:append() {
    cat ${UNPACKDIR}/config.in > $CONFIG
}
