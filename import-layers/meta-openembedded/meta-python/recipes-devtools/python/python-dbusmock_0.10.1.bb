# Copyright (c) 2014 LG Electronics, Inc.

SUMMARY = "With this program/Python library you can easily create mock objects on D-Bus"
AUTHOR = "Martin Pitt <martin.pitt@ubuntu.com>"
SECTION = "devel/python"

LICENSE = "GPL-3.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=e6a600fd5e1d9cbde2d983680233ad02"

DEPENDS += "python-pygobject python-dbus"

SRC_URI = "https://launchpad.net/${BPN}/trunk/${PV}/+download/${BP}.tar.gz"
SRC_URI[md5sum] = "7370d325c4a75494dd71885ca65b79e8"
SRC_URI[sha256sum] = "03aadc93bdc26ea18d4d78fcff7b6cb34f4e18623bc5cc41cf9539d663cee11e"

SRC_URI += " \
    file://0001-Add-functionality-to-add-own-objects-to-internal-obj.patch \
    file://0002-Add-possibility-to-import-templates-from-packages.patch \
"

inherit setuptools
