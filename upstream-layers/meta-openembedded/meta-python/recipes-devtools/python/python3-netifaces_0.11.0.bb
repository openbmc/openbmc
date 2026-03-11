DESCRIPTION = "Portable network interface information for Python"
SECTION = "devel/python"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=a53cbc7cb75660694e138ba973c148df"

inherit pypi setuptools3

SRC_URI += "file://0001-netifaces-initialize-msghdr-in-a-portable-way.patch"

SRC_URI[sha256sum] = "043a79146eb2907edf439899f262b3dfe41717d34124298ed281139a8b93ca32"
