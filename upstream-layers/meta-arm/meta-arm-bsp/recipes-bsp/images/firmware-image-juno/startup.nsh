echo -off
echo Juno startup.nsh from NOR flash
echo Example command to start the kernel:
echo norkern dtb=board.dtb initrd=ramdisk.img console=ttyAMA0,115200n8 root=/dev/sda2 rw rootwait earlyprintk=pl011,0x7ff80000 debug user_debug=31 androidboot.hardware=juno loglevel=9 sky2.mac_address=0xAA,0xBB,0xCC,0xDD,0xEE,0xFF
