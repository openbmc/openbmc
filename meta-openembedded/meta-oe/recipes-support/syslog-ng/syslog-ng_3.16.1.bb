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

SRC_URI[md5sum] = "72d44ad02c2e9ba0748b3ecd3f15a7ff"
SRC_URI[sha256sum] = "c7ee6f1d5e98d86f191964e580111bfa71081ecbb3275cea035bbba177b73a29"
