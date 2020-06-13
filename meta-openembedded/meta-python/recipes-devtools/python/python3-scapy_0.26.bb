DESCRIPTION = "Packet crafting/sending/sniffing, PCAP processing tool,\
based on scapy with python3 compatibility"
SECTION = "devel/python"
HOMEPAGE = "https://github.com/phaethon/scapy"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=a88f5c4e1c935f295ebeaf51fc8644ef"

inherit pypi setuptools3

PYPI_PACKAGE = "scapy-python3"

SRC_URI[md5sum] = "513469447735a4a416d495f63e28aa4b"
SRC_URI[sha256sum] = "81e4f5522d38c136fd3f1be4e35ffe4fd1f4c2091db3c021d95f8b9d5978b9eb"
