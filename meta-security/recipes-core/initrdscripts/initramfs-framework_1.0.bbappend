require ${@bb.utils.contains('IMAGE_CLASSES', 'dm-verity', 'initramfs-framework.inc', '', d)}
