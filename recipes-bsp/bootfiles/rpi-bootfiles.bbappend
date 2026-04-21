do_deploy:append() {
    install -m 0644 ${DEPLOY_DIR_IMAGE}/u-boot.bin ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}
}

do_deploy[depends] += "virtual/bootloader:do_deploy"
