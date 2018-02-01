require ${BPN}.inc

SRCREV = "v1_1_5"
LIC_FILES_CHKSUM = "file://LICENCE.miniupnpd;md5=b0dabf9d8e0f871554e309d62ead8d2b"

PNBLACKLIST[minidlna] ?= "Fails to build with RSS http://errors.yoctoproject.org/Errors/Details/130630/ - the recipe will be removed on 2017-09-01 unless the issue is fixed"
