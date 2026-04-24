#!/bin/bash

done_func() {
echo "vl805: Nothing to do; Already here."
}

flash_func() {
/opt/via805/vl805_flash.sh flash
}

main_func() {
lsusb | grep -qie "via.*labs" && done_func || flash_func
}

eval $(fw_printenv mode)
echo "vl805 flash mode=${mode}" > /dev/kmsg
if [ ${mode} = "atp" ];then
main_func | tee /dev/kmsg
fi
