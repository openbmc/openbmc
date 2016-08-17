require gnome-doc-utils.inc
LIC_FILES_CHKSUM = "file://COPYING.GPL;md5=eb723b61539feef013de476e68b5c50a \
		    file://COPYING.LGPL;md5=a6f89e2100d9b6cdffcea4f398e37343"

SRC_URI += "file://xsltproc_nonet.patch \
            file://use-usr-bin-env-for-python-in-xml2po.patch \
            file://sysrooted-pkg-config.patch \
           "

SRC_URI[archive.md5sum] = "3c64ad7bacd617b04999e4a168afaac5"
SRC_URI[archive.sha256sum] = "cb0639ffa9550b6ddf3b62f3b1add92fb92ab4690d351f2353cffe668be8c4a6"
