# Copyright (C) 2012 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

require kmod.inc

DEPENDS += "zlib-native"

inherit native

SRC_URI += "file://Change-to-calling-bswap_-instead-of-htobe-and-be-toh.patch \
           "

do_install_append (){
	for tool in depmod insmod lsmod modinfo modprobe rmmod
	do
		ln -s kmod ${D}${bindir}/$tool
	done
}
