require syslog-ng.inc

# We only want to add stuff we need to the defaults provided in syslog-ng.inc.
#
SRC_URI += "https://github.com/balabit/syslog-ng/releases/download/${BP}/${BP}.tar.gz \
           file://syslog-ng.conf.systemd \
           file://syslog-ng.conf.sysvinit \
           file://initscript \
           file://volatiles.03_syslog-ng \
           file://syslog-ng-tmp.conf \
           file://syslog-ng.service-the-syslog-ng-service.patch \
           file://0002-scl-fix-wrong-ownership-during-installation.patch \
           file://0005-.py-s-python-python3-exclude-tests.patch \
           "
SRC_URI[md5sum] = "69ef4dc5628d5e603e9e4a1b937592f8"
SRC_URI[sha256sum] = "2eeb8e0dbbcb556fdd4e50bc9f29bc8c66c9b153026f87caa7567bd3139c186a"
