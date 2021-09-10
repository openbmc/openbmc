require ${@bb.utils.contains('IMAGE_CLASSES', 'dm-verity-img', 'initramfs-framework.inc', '', d)}
