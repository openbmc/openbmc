require pseudo.inc

SRC_URI = "http://downloads.yoctoproject.org/releases/pseudo/${BPN}-${PV}.tar.bz2 \
           file://0001-configure-Prune-PIE-flags.patch \
           file://fallback-passwd \
           file://fallback-group \
           file://moreretries.patch \
           file://efe0be279901006f939cd357ccee47b651c786da.patch \
           file://b6b68db896f9963558334aff7fca61adde4ec10f.patch \
           file://fastopreply.patch \
           file://toomanyfiles.patch \
           file://0001-Use-epoll-API-on-Linux.patch \
           "

SRC_URI[md5sum] = "7d41e72188fbea1f696c399c1a435675"
SRC_URI[sha256sum] = "ceb456bd47770a37ca20784a91d715c5a7601e07e26ab11b0c77e9203ed3d196"
