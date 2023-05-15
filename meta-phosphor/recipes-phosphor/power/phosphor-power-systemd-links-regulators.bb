SUMMARY = "Phosphor Power Regulator services installation"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"
PR = "r1"

inherit allarch

RDEPENDS:${PN} += "phosphor-power-regulators"

pkg_prerm:${PN}() {
        LINK="$D$systemd_system_unitdir/obmc-chassis-poweron@${i}.target.requires/phosphor-regulators-config.service"
        rm $LINK

        LINK="$D$systemd_system_unitdir/obmc-chassis-poweron@${i}.target.wants/phosphor-regulators-monitor-enable.service"
        rm $LINK

        LINK="$D$systemd_system_unitdir/obmc-chassis-poweroff@${i}.target.wants/phosphor-regulators-monitor-disable.service"
        rm $LINK
}
pkg_postinst:${PN}() {
        mkdir -p $D$systemd_system_unitdir/obmc-chassis-poweron@0.target.requires
        LINK="$D$systemd_system_unitdir/obmc-chassis-poweron@0.target.requires/phosphor-regulators-config.service"
        TARGET="../phosphor-regulators-config.service"
        ln -s $TARGET $LINK

        mkdir -p $D$systemd_system_unitdir/obmc-chassis-poweron@0.target.wants
        LINK="$D$systemd_system_unitdir/obmc-chassis-poweron@0.target.wants/phosphor-regulators-monitor-enable.service"
        TARGET="../phosphor-regulators-monitor-enable.service"
        ln -s $TARGET $LINK

        mkdir -p $D$systemd_system_unitdir/obmc-chassis-poweroff@0.target.wants
        LINK="$D$systemd_system_unitdir/obmc-chassis-poweroff@0.target.wants/phosphor-regulators-monitor-disable.service"
        TARGET="../phosphor-regulators-monitor-disable.service"
        ln -s $TARGET $LINK
}

ALLOW_EMPTY:${PN} = "1"
