DESCRIPTION = "Acpitool is a Linux ACPI client. It simply reads /proc/acpi or /sys/class entries \
and presents the output in a meaningfull, human-readable format."
HOMEPAGE = "http://freeunix.dyndns.org:8088/site2/acpitool.shtml"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

SRC_URI = "http://nchc.dl.sourceforge.net/project/${BPN}/${BPN}/${PV}/${BP}.tar.bz2 \
    "

inherit autotools

SRC_URI[md5sum] = "9e4ec55201be0be71ffbc56d38b42b57"
SRC_URI[sha256sum] = "004fb6cd43102918b6302cf537a2db7ceadda04aef2e0906ddf230f820dad34f"
