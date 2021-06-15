#! /bin/bash

# Call using:
#../microblaze/sysroots/x86_64-oesdk-linux/usr/bin/microblaze-xilinx-elf/microblaze-xilinx-elf-gcc -print-multi-lib | mb-convert-head.sh

# Then copy the output into the special microblaze-tc BSP

sed -e 's,;, ,' |
  while read mlib args ; do
    if [ $mlib = '.' ]; then
       continue
    fi
    multilib="libmb$(echo $mlib | sed -e 's,/,,g')"

    echo 'MULTILIBS += "multilib:'${multilib}'"'
  done
