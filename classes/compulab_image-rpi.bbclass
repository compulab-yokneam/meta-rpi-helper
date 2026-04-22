ROOTFS_POSTPROCESS_COMMAND += " compulab_rootfs_updateg ; "

compulab_rootfs_updateg() {
	src_path="${@bb.utils.which(d.getVar('BBPATH'), "classes/compulab_image-rpi.bbclass")}"
    src_path=$(dirname ${src_path})/files
    cp ${src_path}/interfaces ${IMAGE_ROOTFS}/etc/network/
    cp ${src_path}/99-usb-net.rules ${IMAGE_ROOTFS}/etc/udev/rules.d/
}

# export bb_path = "${@bb.utils.which(d.getVar('BBPATH'), "classes/compulab_image-rpi.bbclass")}"
compulab_files() {
	src_path="${@bb.utils.which(d.getVar('BBPATH'), "classes/compulab_image-rpi.bbclass")}"
    src_path=$(dirname ${src_path})/files
}
