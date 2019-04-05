require syslog-ng.inc

SRC_URI = "https://github.com/balabit/syslog-ng/releases/download/${BP}/${BP}.tar.gz \
           file://syslog-ng.conf.systemd \
           file://syslog-ng.conf.sysvinit \
           file://initscript \
           file://volatiles.03_syslog-ng \
           file://configure.ac-add-option-enable-thread-tls-to-manage-.patch \
           file://fix-config-libnet.patch \
           file://fix-invalid-ownership.patch \
           file://syslog-ng.service-the-syslog-ng-service.patch \
           file://0001-syslog-ng-fix-segment-fault-during-service-start.patch \
           "

SRC_URI[md5sum] = "aa79bc13d9fd925aa5fb9516e87aacd3"
SRC_URI[sha256sum] = "5cf931a9d7bead0e6d9a2c65eee8f6005a005878f59aa280f3c4294257ed5178"
