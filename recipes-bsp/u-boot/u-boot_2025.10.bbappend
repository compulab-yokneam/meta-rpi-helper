FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += " \
    file://0001-Update-for-rpi4-on-CompuLab-boards.patch \
    file://compulab.cfg \
"
