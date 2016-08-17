SUMMARY = "All packages for full XFCE installation"
SECTION = "x11/wm"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/LICENSE;md5=4d92cd373abda3937c2bc47fbc49d690"

PR = "r10"

inherit packagegroup

# mandatory
RDEPENDS_${PN} = " \
    packagegroup-xfce-base \
"

# nice to have
RRECOMMENDS_${PN} = " \
    xfwm4-theme-daloa \
    xfwm4-theme-kokodi \
    xfwm4-theme-moheli \
    \
    xfce-dusk-gtk3 \
    \
    xfce4-cpufreq-plugin \
    xfce4-cpugraph-plugin \
    xfce4-datetime-plugin \
    xfce4-eyes-plugin \
    xfce4-clipman-plugin \
    xfce4-diskperf-plugin \
    xfce4-netload-plugin \
    xfce4-genmon-plugin \
    xfce4-xkb-plugin \
    xfce4-wavelan-plugin \
    xfce4-places-plugin \
    xfce4-systemload-plugin \
    xfce4-time-out-plugin \
    xfce4-timer-plugin \
    xfce4-embed-plugin \
    xfce4-weather-plugin \
    xfce4-fsguard-plugin \
    xfce4-battery-plugin \
    xfce4-mount-plugin \
    xfce4-powermanager-plugin \
    xfce4-closebutton-plugin \
    xfce4-equake-plugin \
    xfce4-notes-plugin \
    xfce4-whiskermenu-plugin \
    xfce4-mailwatch-plugin \
    xfce4-kbdleds-plugin \
    xfce4-smartbookmark-plugin \
    xfce4-hotcorner-plugin \
    ${@bb.utils.contains('DISTRO_FEATURES', 'pulseaudio', 'xfce4-pulseaudio-plugin', '', d)} \
    xfce4-sensors-plugin \
    xfce4-calculator-plugin \
    xfce4-verve-plugin \
    \
    xfce-polkit \
    \
    thunar-media-tags-plugin \
    thunar-archive-plugin \
    \
    xfce4-appfinder \
    xfce4-screenshooter \
    xfce4-power-manager \
    xfce4-mixer \
    ristretto \
    xfce4-taskmanager \
    gigolo \
"
