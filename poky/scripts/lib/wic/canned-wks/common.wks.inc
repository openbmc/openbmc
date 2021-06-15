# This file is included into 3 canned wks files from this directory
part /boot --source bootimg-pcbios --ondisk sda --label boot --active --align 1024
part / --source rootfs --use-uuid --fstype=ext4 --mkfs-extraopts "-T default" --label platform --align 1024
