# This file is included into 3 canned wks files from this directory
part /boot --source bootimg-pcbios --ondisk sda --label boot --active --align 1024
part / --source rootfs --ondisk sda --fstype=ext4 --label platform --align 1024
