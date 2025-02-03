SUMMARY = "Meta-oe ptest packagegroups"

PACKAGE_ARCH = "${MACHINE_ARCH}"
inherit packagegroup

PROVIDES = "${PACKAGES}"
PACKAGES = "\
    packagegroup-meta-oe \
    packagegroup-meta-oe-benchmarks \
    packagegroup-meta-oe-bsp \
    packagegroup-meta-oe-connectivity \
    packagegroup-meta-oe-core \
    packagegroup-meta-oe-crypto \
    packagegroup-meta-oe-dbs \
    packagegroup-meta-oe-devtools \
    packagegroup-meta-oe-extended \
    packagegroup-meta-oe-kernel \
    packagegroup-meta-oe-multimedia \
    packagegroup-meta-oe-navigation \
    packagegroup-meta-oe-printing \
    packagegroup-meta-oe-security \
    packagegroup-meta-oe-shells \
    packagegroup-meta-oe-support \
    packagegroup-meta-oe-test \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "packagegroup-meta-oe-gnome", "", d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "packagegroup-meta-oe-graphics", "", d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "ptest", "packagegroup-meta-oe-ptest-packages", "", d)} \
"
#PACKAGES += "packagegroup-meta-oe-fortran-packages"

RDEPENDS:packagegroup-meta-oe = "\
    packagegroup-meta-oe-benchmarks \
    packagegroup-meta-oe-bsp \
    packagegroup-meta-oe-connectivity \
    packagegroup-meta-oe-core \
    packagegroup-meta-oe-crypto \
    packagegroup-meta-oe-dbs \
    packagegroup-meta-oe-devtools \
    packagegroup-meta-oe-extended \
    packagegroup-meta-oe-kernel \
    packagegroup-meta-oe-multimedia \
    packagegroup-meta-oe-navigation \
    packagegroup-meta-oe-printing \
    packagegroup-meta-oe-security \
    packagegroup-meta-oe-shells \
    packagegroup-meta-oe-support \
    packagegroup-meta-oe-test \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "packagegroup-meta-oe-gnome", "", d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "packagegroup-meta-oe-graphics", "", d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "ptest", "packagegroup-meta-oe-ptest-packages", "", d)} \
"

RDEPENDS:packagegroup-meta-oe-benchmarks = "\
    bonnie++ \
    dbench \
    dhrystone \
    fio \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11 wayland opengl", "glmark2", "", d)} \
    iozone3 \
    iperf2 \
    iperf3 \
    libc-bench \
    linpack \
    lmbench \
    mbw \
    memtester \
    nbench-byte \
    phoronix-test-suite \
    qperf \
    s-suite \
    stressapptest \
    tinymembench \
    tiobench \
    whetstone \
"
RDEPENDS:packagegroup-meta-oe-benchmarks:append:armv7a = " cpuburn-arm sysbench"
RDEPENDS:packagegroup-meta-oe-benchmarks:append:armv7ve = " cpuburn-arm sysbench"
RDEPENDS:packagegroup-meta-oe-benchmarks:append:aarch64 = " cpuburn-arm sysbench"
RDEPENDS:packagegroup-meta-oe-benchmarks:append:x86 = " sysbench"
RDEPENDS:packagegroup-meta-oe-benchmarks:append:x86-64 = " sysbench"
RDEPENDS:packagegroup-meta-oe-benchmarks:append:mips = " sysbench"

RDEPENDS:packagegroup-meta-oe-benchmarks:remove:mipsarch = "libhugetlbfs"
RDEPENDS:packagegroup-meta-oe-benchmarks:remove:mips64 = "tinymembench"
RDEPENDS:packagegroup-meta-oe-benchmarks:remove:mips64el = "tinymembench"
RDEPENDS:packagegroup-meta-oe-benchmarks:remove:riscv64 = "libhugetlbfs"
RDEPENDS:packagegroup-meta-oe-benchmarks:remove:riscv32 = "libhugetlbfs"

RDEPENDS:packagegroup-meta-oe-bsp = "\
    acpitool \
    cpufrequtils \
    edac-utils \
    ${@bb.utils.contains("DISTRO_FEATURES", "systemd", "firmwared", "", d)} \
    flashrom \
    fwupd \
    fwupd-efi \
    irda-utils \
    lmsensors \
    lmsensors-config-cgi \
    lmsensors-config-fancontrol \
    lmsensors-config-sensord \
    lsscsi \
    nvme-cli \
    pcmciautils \
    pointercal \
"
RDEPENDS:packagegroup-meta-oe-bsp:append:x86 = " ledmon"
RDEPENDS:packagegroup-meta-oe-bsp:append:x86-64 = " ledmon"

RDEPENDS:packagegroup-meta-oe-bsp:remove:libc-musl = "ledmon"
RDEPENDS:packagegroup-meta-oe-bsp:remove:mipsarch = "efivar efibootmgr fwupd fwupd-efi"
RDEPENDS:packagegroup-meta-oe-bsp:remove:powerpc = "efivar efibootmgr fwupd fwupd-efi"
RDEPENDS:packagegroup-meta-oe-bsp:remove:powerpc64 = "efivar efibootmgr fwupd fwupd-efi"
RDEPENDS:packagegroup-meta-oe-bsp:remove:powerpc64le = "efivar efibootmgr fwupd fwupd-efi"
RDEPENDS:packagegroup-meta-oe-bsp:remove:riscv64 = "efivar efibootmgr fwupd fwupd-efi"
RDEPENDS:packagegroup-meta-oe-bsp:remove:riscv32 = "efivar efibootmgr fwupd fwupd-efi"

RDEPENDS:packagegroup-meta-oe-connectivity = "\
    cyrus-sasl \
    czmq \
    gammu \
    gattlib \
    gensio \
    hostapd \
    ifplugd \
    irssi \
    iwd \
    krb5 \
    libev \
    libimobiledevice \
    libmbim \
    libmtp \
    libndp \
    libnet \
    libqmi \
    libtorrent \
    libuv \
    libwebsockets \
    linuxptp \
    loudmouth \
    modemmanager \
    mosh \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "obex-data-server", "", d)} \
    obexftp \
    openobex \
    packagegroup-tools-bluetooth \
    paho-mqtt-c \
    paho-mqtt-cpp \
    rabbitmq-c \
    rfkill \
    rtorrent \
    ser2net \
    smstools3 \
    telepathy-glib \
    thrift \
    usbmuxd \
    wifi-test-suite \
    zabbix \
    zeromq \
"

RDEPENDS:packagegroup-meta-oe-connectivity:append:libc-glibc = " wvstreams wvdial"

# dracut needs dracut
RDEPENDS:packagegroup-meta-oe-core = "\
    ${@bb.utils.contains("DISTRO_FEATURES", "systemd", "dbus-broker", "", d)} \
    dbus-cxx \
    dbus-daemon-proxy \
    distro-feed-configs \
    emlog \
    glibmm \
    kernel-module-emlog \
    libdbus-c++ \
    libnfc \
    libsigc++-2.0 \
    libsigc++-3 \
    mdbus2 \
    mm-common \
    ${@bb.utils.contains("DISTRO_FEATURES", "systemd", "ndctl", "", d)} \
    pim435 \
    proxy-libintl \
    safec \
    sdbus-c++ \
    sdbus-c++-tools \
    toybox \
    usleep \
"
RDEPENDS:packagegroup-meta-oe-core:append:libc-glibc = " ${@bb.utils.contains("DISTRO_FEATURES", "x11 opengl", "glfw", "", d)}"
RDEPENDS:packagegroup-meta-oe-core:remove:riscv64 = "safec"
RDEPENDS:packagegroup-meta-oe-core:remove:riscv32 = "safec"

RDEPENDS:packagegroup-meta-oe-crypto = "\
    botan \
    cryptsetup \
    fsverity-utils \
    libkcapi \
    libmcrypt \
    libsodium \
    pkcs11-helper \
"
RDEPENDS:packagegroup-meta-oe-crypto:remove:riscv32 = "botan"

RDEPENDS:packagegroup-meta-oe-dbs = "\
    influxdb \
    leveldb \
    libdbi \
    lmdb \
    mariadb \
    postgresql \
    psqlodbc \
    rocksdb \
    soci \
"
RDEPENDS:packagegroup-meta-oe-dbs:remove:libc-musl:powerpc = "rocksdb"

RDEPENDS:packagegroup-meta-oe-devtools = "\
    abseil-cpp \
    android-tools \
    android-tools-conf \
    apitrace \
    breakpad \
    capnproto-compiler \
    cgdb \
    cjson \
    cloc \
    concurrencykit \
    cscope \
    ctags \
    dbd-mariadb \
    debootstrap \
    dmalloc \
    ${@bb.utils.contains("PACKAGE_CLASSES", "package_rpm", "dnf-plugin-tui", "", d)} \
    doxygen \
    flatbuffers \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "geany-plugins geany", "", d)} \
    giflib \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "glade", "", d)} \
    grpc \
    guider \
    heaptrack \
    icon-slicer \
    ipc-run \
    iptraf-ng \
    jemalloc \
    jq \
    jsoncpp \
    jsonrpc \
    json-schema-validator \
    json-spirit \
    kconfig-frontends \
    lemon \
    libdbi-perl \
    libdev-checklib-perl \
    libgee \
    libio-pty-perl \
    libjson-perl \
    libparse-yapp-perl \
    libperlio-gzip-perl \
    libsombok3 \
    libubox \
    libxerces-c \
    lshw \
    ltrace \
    luajit \
    luaposix \
    mcpp \
    memstat \
    mercurial \
    microsoft-gsl \
    mpich \
    msgpack-c \
    msgpack-cpp \
    nodejs \
    openocd \
    pax-utils \
    php \
    ply \
    poke \
    protobuf \
    protobuf-c \
    pugixml \
    python3-distutils-extra \
    python3-pycups \
    rapidjson \
    serialcheck \
    squashfs-tools-ng \
    tclap \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "tk", "", d)} \
    uftrace \
    uw-imap \
    valijson \
    xerces-c-samples \
    xmlrpc-c \
    yajl \
    yasm \
"
RDEPENDS:packagegroup-meta-oe-devtools:append:x86 = " cpuid msr-tools pahole pmtools"
RDEPENDS:packagegroup-meta-oe-devtools:append:x86-64 = " cpuid msr-tools pahole pcimem pmtools"
RDEPENDS:packagegroup-meta-oe-devtools:append:riscv64 = " pcimem"
RDEPENDS:packagegroup-meta-oe-devtools:append:arm = " pcimem"
RDEPENDS:packagegroup-meta-oe-devtools:append:aarch64 = " pahole pcimem"
RDEPENDS:packagegroup-meta-oe-devtools:append:libc-musl = " musl-nscd"

RDEPENDS:packagegroup-meta-oe-devtools:remove:arm = "concurrencykit"
RDEPENDS:packagegroup-meta-oe-devtools:remove:armv5 = "uftrace nodejs"
RDEPENDS:packagegroup-meta-oe-devtools:remove:mipsarch = "concurrencykit lshw ply uftrace"
RDEPENDS:packagegroup-meta-oe-devtools:remove:mips64 = "luajit nodejs"
RDEPENDS:packagegroup-meta-oe-devtools:remove:mips64el = "luajit nodejs"
RDEPENDS:packagegroup-meta-oe-devtools:remove:powerpc = "android-tools breakpad lshw luajit uftrace"
RDEPENDS:packagegroup-meta-oe-devtools:remove:powerpc64 = "android-tools breakpad lshw luajit ply uftrace"
RDEPENDS:packagegroup-meta-oe-devtools:remove:powerpc64le = "android-tools breakpad lshw luajit ply uftrace"
RDEPENDS:packagegroup-meta-oe-devtools:remove:riscv64 = "breakpad concurrencykit heaptrack lshw ltrace luajit nodejs ply"
RDEPENDS:packagegroup-meta-oe-devtools:remove:riscv32 = "breakpad concurrencykit heaptrack lshw ltrace luajit nodejs ply uftrace"
RDEPENDS:packagegroup-meta-oe-devtools:remove:libc-musl:riscv32 = "php"
RDEPENDS:packagegroup-meta-oe-devtools:remove:aarch64 = "concurrencykit"
RDEPENDS:packagegroup-meta-oe-devtools:remove:x86 = "ply"

RDEPENDS:packagegroup-meta-oe-extended = "\
    bitwise \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11 wayland opengl", "boinc-client", "", d)} \
    brotli \
    byacc \
    can-utils \
    canutils \
    cmatrix \
    cmpi-bindings \
    collectd \
    ddrescue \
    dialog \
    dlt-daemon \
    docopt.cpp \
    duktape \
    dumb-init \
    enscript \
    figlet \
    fluentbit \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "gnuplot", "", d)} \
    haveged \
    hexedit \
    hiredis \
    hplip \
    hwloc \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "icewm", "", d)} \
    indent \
    iotop \
    isomd5sum \
    jansson \
    jpnevulator \
    konkretcmpi \
    libblockdev \
    libcec \
    libconfig \
    libdivecomputer \
    libfastjson \
    libfile-fnmatch-perl \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "libgxim", "", d)} \
    libidn \
    libleak \
    liblockfile \
    liblogging \
    liblognorm \
    libmodbus \
    libplist \
    libpwquality \
    libqb \
    librelp \
    ${@bb.utils.contains("DISTRO_FEATURES", "systemd", "libreport", "", d)} \
    libserialport \
    libsigrok \
    libsigrokdecode \
    libsocketcan \
    libstatgrab \
    libuio \
    libusbmuxd \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "libwmf", "", d)} \
    libyang \
    libzip \
    linuxconsole \
    lockfile-progs \
    logwatch \
    lprng \
    md5deep \
    mraa \
    nana \
    nicstat \
    ${@bb.utils.contains("DISTRO_FEATURES", "pam", "openwsman", "", d)} \
    ostree \
    7zip \
    ${@bb.utils.contains("DISTRO_FEATURES", "pam", "pam-plugin-ccreds pam-plugin-ldapdb pam-ssh-agent-auth", "", d)} \
    pegtl \
    ${@bb.utils.contains("DISTRO_FEATURES", "polkit", "polkit-group-rule-datetime polkit-group-rule-network polkit", "", d)} \
    rarpd \
    redis \
    redis-plus-plus \
    rrdtool \
    sanlock \
    sblim-cmpi-devel \
    ${@bb.utils.contains("DISTRO_FEATURES", "pam", "sblim-sfcb ", "", d)} \
    sblim-sfcc \
    sblim-sfc-common \
    scsirastools \
    sedutil \
    sgpio \
    sigrok-cli \
    smartmontools \
    s-nail \
    snappy \
    tipcutils \
    tiptop \
    tmate \
    tmux \
    triggerhappy \
    uml-utilities \
    upm \
    vlock \
    volume-key \
    wipe \
    wxwidgets \
    zlog \
    zram \
    zstd \
    zsync-curl \
"
RDEPENDS:packagegroup-meta-oe-extended:append:libc-musl = " libexecinfo"
RDEPENDS:packagegroup-meta-oe-extended:append:x86-64 = " pmdk libx86-1"
RDEPENDS:packagegroup-meta-oe-extended:append:x86 = " libx86-1"

RDEPENDS:packagegroup-meta-oe-extended:remove:libc-musl = "sysdig"
RDEPENDS:packagegroup-meta-oe-extended:remove:mipsarch = "upm mraa minifi-cpp tiptop"
RDEPENDS:packagegroup-meta-oe-extended:remove:mips = "sysdig"
RDEPENDS:packagegroup-meta-oe-extended:remove:powerpc = "upm mraa minifi-cpp"
RDEPENDS:packagegroup-meta-oe-extended:remove:powerpc64 = "upm mraa minifi-cpp"
RDEPENDS:packagegroup-meta-oe-extended:remove:powerpc64le = "upm mraa sysdig"
RDEPENDS:packagegroup-meta-oe-extended:remove:riscv64 = "upm libleak mraa sysdig tiptop"
RDEPENDS:packagegroup-meta-oe-extended:remove:riscv32 = "upm libleak mraa sysdig tiptop"

RDEPENDS:packagegroup-meta-oe-gnome = "\
    atkmm \
    gcab \
    gmime \
    gnome-common \
    gnome-theme-adwaita \
    gtk+ \
    gtkmm \
    gtkmm3 \
    libjcat \
    pyxdg \
"

RDEPENDS:packagegroup-meta-oe-graphics = "\
    bdftopcf \
    cairomm \
    deqp-runner \
    dietsplash \
    directfb \
    directfb-examples \
    ${@bb.utils.contains("PACKAGE_CLASSES", "package_rpm", "dnfdragora", "", d)} \
    fbgrab \
    fbida \
    feh \
    font-adobe-100dpi \
    font-adobe-utopia-100dpi \
    font-bh-100dpi \
    font-bh-lucidatypewriter-100dpi \
    font-bitstream-100dpi \
    font-cursor-misc \
    fontforge \
    font-misc-misc \
    ${@bb.utils.contains("DISTRO_FEATURES", "opengl", "freeglut", "", d)} \
    ftgl \
    fvwm \
    gphoto2 \
    graphviz \
    gtkperf \
    gtkwave \
    iceauth \
    imlib2 \
    jasper \
    leptonica \
    libforms \
    libgphoto2 \
    libmng \
    libsdl \
    libsdl2-image \
    libsdl2-mixer \
    libsdl2-net \
    ${@bb.utils.contains("DISTRO_FEATURES", "opengl", "libsdl2-ttf", "", d)} \
    libsdl-gfx \
    libsdl-image \
    libsdl-mixer \
    libsdl-net \
    libsdl-ttf \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "libvdpau vdpauinfo", "", d)} \
    libvncserver \
    libxaw6 \
    libxpresent \
    libyui \
    libyui-ncurses \
    lvgl \
    lxdm \
    numlockx \
    nyancat \
    obconf \
    openbox \
    ${@bb.utils.contains("DISTRO_FEATURES", "opengl", "opengl-es-cts", "", d)} \
    openjpeg \
    packagegroup-fonts-truetype \
    pangomm \
    parallel-deqp-runner \
    qrencode \
    sessreg \
    setxkbmap \
    source-code-pro-fonts \
    source-han-sans-cn-fonts \
    source-han-sans-jp-fonts \
    source-han-sans-kr-fonts \
    source-han-sans-tw-fonts \
    spirv-shader-generator \
    spirv-tools \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "st", "", d)} \
    stalonetray \
    surf \
    terminus-font-consolefonts \
    terminus-font-pcf \
    tesseract \
    tesseract-lang \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11 pam", "tigervnc", "", d)} \
    tslib \
    ttf-abyssinica \
    ttf-arphic-uming \
    ttf-dejavu-common \
    ttf-dejavu-mathtexgyre \
    ttf-dejavu-sans \
    ttf-dejavu-sans-condensed \
    ttf-dejavu-sans-mono \
    ttf-dejavu-serif \
    ttf-dejavu-serif-condensed \
    ttf-droid-sans \
    ttf-droid-sans-fallback \
    ttf-droid-sans-japanese \
    ttf-droid-sans-mono \
    ttf-droid-serif \
    ttf-gentium \
    ttf-hunky-sans \
    ttf-hunky-serif \
    ttf-inconsolata \
    ttf-ipag \
    ttf-ipagp \
    ttf-ipam \
    ttf-ipamp \
    ttf-liberation-mono \
    ttf-liberation-sans \
    ttf-liberation-sans-narrow \
    ttf-liberation-serif \
    ttf-lklug \
    ttf-lohit \
    ttf-noto-emoji-color \
    ttf-noto-emoji-regular \
    ttf-pt-sans \
    ttf-roboto \
    ttf-sazanami-gothic \
    ttf-sazanami-mincho \
    ttf-takao-gothic \
    ttf-takao-mincho \
    ttf-takao-pgothic \
    ttf-takao-pmincho \
    ttf-tlwg \
    ttf-ubuntu-mono \
    ttf-ubuntu-sans \
    ttf-vlgothic \
    ttf-wqy-zenhei \
    twm \
    unclutter-xfixes \
    ${@bb.utils.contains("DISTRO_FEATURES", "opengl vulkan", "vulkan-cts", "", d)} \
    x11vnc \
    xcb-util-cursor \
    xclock \
    xcursorgen \
    xdotool \
    xf86-input-tslib \
    xf86-input-void \
    xf86-video-armsoc \
    xf86-video-ati \
    xfontsel \
    xgamma \
    xkbevd \
    xkbprint \
    xkbutils \
    xlsatoms \
    xlsclients \
    xlsfonts \
    xmag \
    xmessage \
    xorg-fonts-100dpi \
    xorg-sgml-doctools \
    xrdb \
    xrefresh \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11 pam", "xscreensaver", "", d)} \
    xsetroot \
    xstdcmap \
    xterm \
    xwd \
    xwud \
    yad \
    ydotool \
"
RDEPENDS:packagegroup-meta-oe-graphics:append:x86 = " renderdoc xf86-video-nouveau xf86-video-mga"
RDEPENDS:packagegroup-meta-oe-graphics:append:x86-64 = " renderdoc xf86-video-nouveau xf86-video-mga"
RDEPENDS:packagegroup-meta-oe-graphics:append:arm = " renderdoc"
RDEPENDS:packagegroup-meta-oe-graphics:append:aarch64 = " renderdoc"

RDEPENDS:packagegroup-meta-oe-graphics:remove:libc-musl = "renderdoc"

RDEPENDS:packagegroup-meta-oe-kernel = "\
    agent-proxy \
    broadcom-bt-firmware \
    cpupower \
    crash \
    ipmitool \
    kernel-selftest \
    minicoredumper \
    oprofile \
    spidev-test \
    trace-cmd \
    usbip-tools \
"
RDEPENDS:packagegroup-meta-oe-kernel:append:x86 = " intel-speed-select ipmiutil pm-graph turbostat"
RDEPENDS:packagegroup-meta-oe-kernel:append:x86-64 = " intel-speed-select ipmiutil pm-graph turbostat bpftool"
RDEPENDS:packagegroup-meta-oe-kernel:append:x86-64:libc-glibc = " kpatch"
RDEPENDS:packagegroup-meta-oe-kernel:append:powerpc64 = " libpfm4"

# Kernel-selftest does not build with 5.8 and its exluded from build too so until its fixed remove it
RDEPENDS:packagegroup-meta-oe-kernel:remove = "kernel-selftest"
RDEPENDS:packagegroup-meta-oe-kernel:remove:libc-musl = "bpftool crash intel-speed-select kernel-selftest minicoredumper turbostat"

RDEPENDS:packagegroup-meta-oe-kernel:remove:mips64 = "crash"
RDEPENDS:packagegroup-meta-oe-kernel:remove:mips64el = "crash"

RDEPENDS:packagegroup-meta-oe-kernel:remove:riscv64 = "crash oprofile"
RDEPENDS:packagegroup-meta-oe-kernel:remove:riscv32 = "crash oprofile"

RDEPENDS:packagegroup-meta-oe-multimedia = "\
    a2jmidid \
    audiofile \
    dirsplit \
    ${@bb.utils.contains("LICENSE_FLAGS_ACCEPTED", "commercial", "faad2", "", d)} \
    genisoimage \
    icedax \
    id3lib \
    jack-server \
    jack-utils \
    libass \
    libcdio \
    libcdio-paranoia \
    libdvdread \
    ${@bb.utils.contains("LICENSE_FLAGS_ACCEPTED", "commercial", "libmad", "", d)} \
    libmikmod \
    libmms \
    libmodplug \
    libopus \
    libopusenc \
    libvpx \
    live555-examples \
    live555-mediaserver \
    ${@bb.utils.contains("LICENSE_FLAGS_ACCEPTED", "commercial", "mpv", "", d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "pavucontrol", "", d)} \
    sound-theme-freedesktop \
    v4l-utils \
    wavpack \
    wodim \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "xsp", "", d)} \
    yavta \
"

RDEPENDS:packagegroup-meta-oe-navigation = "\
    geoclue \
    geos \
    ${@bb.utils.contains("DISTRO_FEATURES", "bluz4", "gpsd-machine-conf gpsd", "", d)} \
    libspatialite \
    proj \
"

RDEPENDS:packagegroup-meta-oe-printing = "\
    cups-filters \
    gutenprint \
    qpdf \
"

RDEPENDS:packagegroup-meta-oe-security = "\
    auditd \
    keyutils \
    nmap \
    ${@bb.utils.contains("DISTRO_FEATURES", "pam", "passwdqc", "", d)} \
    softhsm \
    tomoyo-tools \
"

RDEPENDS:packagegroup-meta-oe-shells = "\
    dash \
    mksh \
    tcsh \
    zsh \
"

RDEPENDS:packagegroup-meta-oe-support = "\
    ace-cloud-editor \
    anthy \
    asio \
    atop \
    augeas \
    avro-c \
    bdwgc \
    c-ares \
    ccid \
    ckermit \
    clinfo \
    cmark \
    ${@bb.utils.contains("DISTRO_FEATURES", "polkit gobject-introspection-data", "colord", "", d)} \
    consolation \
    c-periphery \
    ctapi-common \
    daemonize \
    daemontools \
    devmem2 \
    dfu-util \
    dhex \
    digitemp \
    dool \
    ${@bb.utils.contains("DISTRO_FEATURES", "systemd", "driverctl", "", d)} \
    eject \
    emacs \
    enca \
    epeg \
    espeak \
    evemu-tools \
    exiv2 \
    fbset \
    fbset-modes \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "fltk", "", d)} \
    frame \
    freerdp \
    function2 \
    funyahoo-plusplus \
    gd \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "geis", "", d)} \
    gengetopt \
    gflags \
    glog \
    googlebenchmark \
    gperftools \
    gpm \
    gradm \
    grail \
    gsl \
    gsoap \
    hddtemp \
    hdf5 \
    hidapi \
    hstr \
    htop \
    hunspell \
    hunspell-dictionaries \
    icyque \
    iksemel \
    imagemagick \
    imapfilter \
    iniparser \
    inotify-tools \
    joe \
    lcms \
    lcov \
    libatasmart \
    libbytesize \
    libcanberra \
    libcereal \
    libcyusbserial \
    libdevmapper \
    libestr \
    libfann \
    libftdi \
    libgit2 \
    libgpiod \
    libgusb \
    libharu \
    libiio \
    libinih \
    libjs-jquery-cookie \
    libjs-jquery-globalize \
    libjs-jquery-icheck \
    libjs-sizzle \
    libmanette \
    libmicrohttpd \
    libmimetic \
    libmxml \
    libnice \
    liboauth \
    liboop \
    libotr \
    libp11 \
    libraw1394 \
    librsync \
    libsass \
    libsmi \
    libsoc \
    libssh \
    libssh2 \
    libtar \
    libteam \
    libtinyxml \
    libtinyxml2 \
    liburing \
    libusb-compat \
    libusbg \
    libusbgx \
    libusbgx-config \
    libutempter \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "links-x11", "links", d)} \
    lockdev \
    log4c \
    log4cpp \
    logwarn \
    lvm2 \
    mailcap \
    mbuffer \
    mg \
    mime-support \
    minini \
    monit \
    mscgen \
    multipath-tools \
    nano \
    neon \
    nmon \
    nspr \
    nss \
    numactl \
    onig \
    openct \
    opencv \
    openldap \
    opensc \
    p910nd \
    pcp \
    pcsc-lite \
    pcsc-tools \
    picocom \
    pidgin \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "pidgin-otr", "", d)} \
    pidgin-sipe \
    pngcheck \
    poco \
    poppler \
    poppler-data \
    portaudio-v19 \
    pps-tools \
    procmail \
    purple-skypeweb \
    pv \
    pxaregs \
    raptor2 \
    rdfind \
    re2 \
    read-edid \
    remmina \
    rsnapshot \
    sassc \
    satyr \
    sdmon \
    sdparm \
    serial-forward \
    sg3-utils \
    sharutils \
    smarty \
    spitools \
    srecord \
    ssiapi \
    stm32flash \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "synergy", "", d)} \
    syslog-ng \
    system-config-keyboard \
    tbb \
    thin-provisioning-tools \
    tokyocabinet \
    tree \
    uchardet \
    ${@bb.utils.contains("DISTRO_FEATURES", "polkit", "udisks2", "", d)} \
    uhubctl \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "uim", "", d)} \
    unicode-ucd \
    unixodbc \
    upower \
    uriparser \
    usb-modeswitch \
    usb-modeswitch-data \
    utouch-evemu \
    utouch-frame \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "utouch-mtview", "", d)} \
    wbxml2 \
    xdelta3 \
    xdg-user-dirs \
    xmlsec1 \
    xmlstarlet \
    yaml-cpp \
    zbar \
    zchunk \
    zile \
"
RDEPENDS:packagegroup-meta-oe-support:append:armv7a = "${@bb.utils.contains("TUNE_FEATURES","neon"," ne10","",d)}"
RDEPENDS:packagegroup-meta-oe-support:append:armv7ve = "${@bb.utils.contains("TUNE_FEATURES","neon"," ne10","",d)}"
RDEPENDS:packagegroup-meta-oe-support:append:aarch64 = " ne10"
RDEPENDS:packagegroup-meta-oe-support:append:x86 = " mcelog mce-inject mce-test vboxguestdrivers"
RDEPENDS:packagegroup-meta-oe-support:append:x86-64 = " mcelog mce-inject mce-test vboxguestdrivers"

RDEPENDS:packagegroup-meta-oe-support:remove:arm = "numactl"
RDEPENDS:packagegroup-meta-oe-support:remove:mipsarch = "gperftools"
RDEPENDS:packagegroup-meta-oe-support:remove:riscv64 = "gperftools uim"
RDEPENDS:packagegroup-meta-oe-support:remove:riscv32 = "gperftools uim"
RDEPENDS:packagegroup-meta-oe-support:remove:powerpc = "libcereal ssiapi tbb"
RDEPENDS:packagegroup-meta-oe-support:remove:powerpc64le = "libcereal ssiapi"
RDEPENDS:packagegroup-meta-oe-support:remove:libc-musl = "pcp"
RDEPENDS:packagegroup-meta-oe-support:remove:libc-musl:powerpc = "gsl"

RDEPENDS:packagegroup-meta-oe-test = "\
    bats \
    cmocka \
    cppunit \
    cpputest \
    cukinia \
    cunit \
    cxxtest \
    evtest \
    fb-test \
    fwts \
    googletest \
    pm-qa \
    testfloat \
"
RDEPENDS:packagegroup-meta-oe-test:remove:libc-musl = "pm-qa"
RDEPENDS:packagegroup-meta-oe-test:remove:arm = "fwts"
RDEPENDS:packagegroup-meta-oe-test:remove:mipsarch = "fwts"
RDEPENDS:packagegroup-meta-oe-test:remove:powerpc = "fwts"
RDEPENDS:packagegroup-meta-oe-test:remove:riscv64 = "fwts"
RDEPENDS:packagegroup-meta-oe-test:remove:riscv32 = "fwts"

RDEPENDS:packagegroup-meta-oe-ptest-packages = "\
    cmocka-ptest \
    hiredis-ptest \
    leveldb-ptest \
    libteam-ptest \
    minicoredumper-ptest \
    oprofile-ptest \
    poco-ptest \
    protobuf-ptest \
    psqlodbc-ptest \
    rsyslog-ptest \
    uthash-ptest \
    zeromq-ptest \
"
RDEPENDS:packagegroup-meta-oe-ptest-packages:append:x86 = " mcelog-ptest"
RDEPENDS:packagegroup-meta-oe-ptest-packages:append:x86-64 = " mcelog-ptest"

RDEPENDS:packagegroup-meta-oe-ptest-packages:remove:riscv64 = "oprofile-ptest"
RDEPENDS:packagegroup-meta-oe-ptest-packages:remove:riscv32 = "oprofile-ptest"
RDEPENDS:packagegroup-meta-oe-ptest-packages:remove:arm = "numactl-ptest"
RDEPENDS:packagegroup-meta-oe-ptest-packages:remove:libc-musl = "minicoredumper-ptest"


RDEPENDS:packagegroup-meta-oe-fortran-packages = "\
    lapack \
    octave \
    suitesparse \
"
# library-only or headers-only packages
# They wont be built as part of images but might be interesting to include
# with dev-pkgs images
#
# opencl-headers sdbus-c++-libsystemd nlohmann-fifo sqlite-orm
# nlohmann-json exprtk liblightmodbus p8platform gnome-doc-utils-stub
# glm ttf-mplus xbitmaps ceres-solver cli11 fftw gnulib libeigen ade
# spdlog span-lite uthash websocketpp catch2 properties-cpp cpp-netlib

# rsyslog conflicts with syslog-ng so its not included here

EXCLUDE_FROM_WORLD = "1"
