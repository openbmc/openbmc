SUMMARY = "Meta-oe ptest packagegroups"

PACKAGE_ARCH = "${MACHINE_ARCH}"
inherit packagegroup

PROVIDES = "${PACKAGES}"
PACKAGES = ' \
    packagegroup-meta-oe \
    packagegroup-meta-oe-benchmarks \
    packagegroup-meta-oe-connectivity \
    packagegroup-meta-oe-connectivity-python2 \
    packagegroup-meta-oe-core \
    packagegroup-meta-oe-crypto \
    packagegroup-meta-oe-bsp \
    packagegroup-meta-oe-dbs \
    packagegroup-meta-oe-dbs-python2 \
    packagegroup-meta-oe-devtools \
    packagegroup-meta-oe-extended \
    packagegroup-meta-oe-extended-python2 \
    packagegroup-meta-oe-kernel \
    packagegroup-meta-oe-multimedia \
    packagegroup-meta-oe-navigation \
    packagegroup-meta-oe-security \
    packagegroup-meta-oe-support \
    packagegroup-meta-oe-support-python2 \
    packagegroup-meta-oe-test \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "packagegroup-meta-oe-gnome", "", d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "packagegroup-meta-oe-graphics", "", d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "ptest", "packagegroup-meta-oe-ptest-packages", "", d)} \
'

RDEPENDS_packagegroup-meta-oe = "\
    packagegroup-meta-oe-benchmarks \
    packagegroup-meta-oe-connectivity \
    ${@bb.utils.contains("BBFILE_COLLECTIONS", "meta-python2", "packagegroup-meta-oe-connectivity-python2", "", d)} \
    packagegroup-meta-oe-core \
    packagegroup-meta-oe-crypto \
    packagegroup-meta-oe-bsp \
    packagegroup-meta-oe-dbs \
    ${@bb.utils.contains("BBFILE_COLLECTIONS", "meta-python2", "packagegroup-meta-oe-dbs-python2", "", d)} \
    packagegroup-meta-oe-devtools \
    packagegroup-meta-oe-extended \
    ${@bb.utils.contains("BBFILE_COLLECTIONS", "meta-python2", "packagegroup-meta-oe-extended-python2", "", d)} \
    packagegroup-meta-oe-kernel \
    packagegroup-meta-oe-multimedia \
    packagegroup-meta-oe-navigation \
    packagegroup-meta-oe-security \
    packagegroup-meta-oe-support \
    ${@bb.utils.contains("BBFILE_COLLECTIONS", "meta-python2", "packagegroup-meta-oe-support-python2", "", d)} \
    packagegroup-meta-oe-test \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "packagegroup-meta-oe-gnome", "", d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "packagegroup-meta-oe-graphics", "", d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "ptest", "packagegroup-meta-oe-ptest-packages", "", d)} \
"

RDEPENDS_packagegroup-meta-oe-benchmarks = "\
    dhrystone iperf2 linpack phoronix-test-suite \
    tiobench bonnie++ fio iperf2 iperf3 lmbench s-suite whetstone \
    libc-bench memtester sysbench dbench iozone3 libhugetlbfs \
    nbench-byte tinymembench \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11 wayland opengl", "glmark2", "", d)} \
"

RDEPENDS_packagegroup-meta-oe-benchmarks_remove_mipsarch = "libhugetlbfs"
RDEPENDS_packagegroup-meta-oe-benchmarks_remove_mips64 = "tinymembench"
RDEPENDS_packagegroup-meta-oe-benchmarks_remove_mips64el = "tinymembench"
RDEPENDS_packagegroup-meta-oe-benchmarks_remove_riscv64 = "libhugetlbfs"
RDEPENDS_packagegroup-meta-oe-benchmarks_remove_riscv32 = "libhugetlbfs"

RDEPENDS_packagegroup-meta-oe-connectivity ="\
    gammu hostapd irssi krb5 libev libimobiledevice \
    libmbim libmtp libndp libqmi libtorrent \
    libuv libwebsockets linuxptp loudmouth \
    modemmanager mosh  \
    paho-mqtt-c phonet-utils rabbitmq-c rfkill rtorrent \
    ser2net smstools3 telepathy-glib thrift \
    usbmuxd zabbix zeromq \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "obex-data-server", "", d)} \
    libmikmod \
    obexftp openobex libnet \
    "
RDEPENDS_packagegroup-meta-oe-connectivity_append_libc-glibc = " wvstreams wvdial"

RDEPENDS_packagegroup-meta-oe-connectivity-python2 = "\
    ${@bb.utils.contains("BBFILE_COLLECTIONS", "meta-python2", "telepathy-idle", "", d)} \
"

# dracut needs dracut
RDEPENDS_packagegroup-meta-oe-core ="\
    dbus-daemon-proxy libdbus-c++ \
    ell glibmm libsigc++-2.0 libxml++ distro-feed-configs \
    mm-common opencl-headers opencl-icd-loader \
    proxy-libintl usleep \
    ${@bb.utils.contains("DISTRO_FEATURES", "systemd", "dbus-broker ndctl", "", d)} \
    "

RDEPENDS_packagegroup-meta-oe-crypto ="\
    botan cryptsetup libkcapi libmcrypt \
    libsodium pkcs11-helper \
    "
RDEPENDS_packagegroup-meta-oe-bsp ="\
    acpitool cpufrequtils edac-utils efibootmgr \
    efivar flashrom lmsensors lmsensors-config \
    lsscsi nvme-cli pcmciautils pointercal \
    "
RDEPENDS_packagegroup-meta-oe-bsp_remove_mipsarch = "efivar efibootmgr"
RDEPENDS_packagegroup-meta-oe-bsp_remove_powerpc = "efivar efibootmgr"
RDEPENDS_packagegroup-meta-oe-bsp_remove_powerpc64 = "efivar efibootmgr"
RDEPENDS_packagegroup-meta-oe-bsp_remove_powerpc64le = "efivar efibootmgr"
RDEPENDS_packagegroup-meta-oe-bsp_remove_riscv64 = "efivar efibootmgr"
RDEPENDS_packagegroup-meta-oe-bsp_remove_riscv32 = "efivar efibootmgr"

RDEPENDS_packagegroup-meta-oe-dbs ="\
    leveldb libdbi mariadb mariadb-native \
    postgresql psqlodbc rocksdb soci \
    sqlite \
    "

RDEPENDS_packagegroup-meta-oe-dbs-python2 ="\
    ${@bb.utils.contains("BBFILE_COLLECTIONS", "meta-python2", "mysql-python", "", d)} \
"

RDEPENDS_packagegroup-meta-oe-devtools ="\
    android-tools android-tools-conf bootchart breakpad \
    capnproto cgdb cscope ctags \
    debootstrap dmalloc flatbuffers \
    giflib grpc guider icon-slicer iptraf-ng jq jsoncpp jsonrpc json-spirit \
    kconfig-frontends lemon libedit libgee libsombok3 \
    libubox log4cplus lshw ltrace lua mcpp memstat mercurial \
    mpich msgpack-c nlohmann-json nodejs openocd pax-utils \
    ipc-run libdbd-mysql-perl libdbi-perl libio-pty-perl php \
    protobuf protobuf-c \
    rapidjson serialcheck sip3 tclap uftrace uw-imap valijson \
    xmlrpc-c yajl yasm \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "geany geany-plugins glade tk", "", d)} \
"

RDEPENDS_packagegroup-meta-oe-devtools_remove_armv5 = "uftrace nodejs"
RDEPENDS_packagegroup-meta-oe-devtools_remove_mipsarch = "uftrace lshw"
RDEPENDS_packagegroup-meta-oe-devtools_remove_mips64 = "nodejs"
RDEPENDS_packagegroup-meta-oe-devtools_remove_mips64el = "nodejs"
RDEPENDS_packagegroup-meta-oe-devtools_remove_powerpc = "android-tools breakpad uftrace lshw"
RDEPENDS_packagegroup-meta-oe-devtools_remove_powerpc64 = "android-tools uftrace lshw"
RDEPENDS_packagegroup-meta-oe-devtools_remove_powerpc64le = "android-tools uftrace lshw"
RDEPENDS_packagegroup-meta-oe-devtools_remove_riscv64 = "breakpad ltrace nodejs uftrace lshw"
RDEPENDS_packagegroup-meta-oe-devtools_remove_riscv32 = "breakpad ltrace nodejs uftrace lshw"

RDEPENDS_packagegroup-meta-oe-extended ="\
    byacc cfengine cfengine-masterfiles cmpi-bindings \
    ddrescue dialog dumb-init enscript fluentbit \
    haveged hexedit hiredis hplip hwloc indent iotop isomd5sum \
    jansson konkretcmpi libblockdev libcec libconfig \
    libdivecomputer libplist libusbmuxd \
    liblockfile liblogging liblognorm libmodbus libmodbus \
    libpwquality libqb libuio \
    lockfile-progs logwatch lprng mailx md5deep \
    mraa nana nicstat \
    p7zip p8platform libfile-fnmatch-perl \
    rarpd redis rrdtool libfastjson librelp rsyslog sanlock \
    sblim-cmpi-devel sblim-sfc-common sblim-sfcc \
    scsirastools sgpio smartmontools snappy \
    can-utils libsocketcan tipcutils tiptop \
    tmux uml-utilities upm vlock volume-key wipe zlog zram \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11 wayland opengl", "boinc-client", "", d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", " libgxim gnuplot libwmf", "", d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "bluez", "collectd", "", d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "pam", "pam-plugin-ccreds pam-plugin-ldapdb", "", d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "pam", "pam-ssh-agent-auth openwsman sblim-sfcb ", "", d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "polkit", "polkit polkit-group-rule-datetime ", "", d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "polkit", "polkit-group-rule-network ", "", d)} \
"
RDEPENDS_packagegroup-meta-oe-extended_remove_mipsarch = "upm mraa tiptop"
RDEPENDS_packagegroup-meta-oe-extended_remove_powerpc = "upm mraa"
RDEPENDS_packagegroup-meta-oe-extended_remove_powerpc64 = "upm mraa"
RDEPENDS_packagegroup-meta-oe-extended_remove_powerpc64le = "upm mraa"
RDEPENDS_packagegroup-meta-oe-extended_remove_riscv64 = "upm mraa tiptop"
RDEPENDS_packagegroup-meta-oe-extended_remove_riscv32 = "upm mraa tiptop"

RDEPENDS_packagegroup-meta-oe-extended-python2 ="\
    ${@bb.utils.contains("BBFILE_COLLECTIONS", "meta-python2", "openlmi-tools", "", d)} \
"

RDEPENDS_packagegroup-meta-oe-gnome ="\
    atkmm gnome-common gnome-doc-utils-stub gtkmm \
    gtkmm3 pyxdg vte9 \
    "

RDEPENDS_packagegroup-meta-oe-graphics ="\
    cairomm dietsplash directfb directfb-examples \
    ${@bb.utils.contains("PACKAGE_CLASSES", "package_rpm", "dnfdragora dnf-plugin-tui", "", d)} \
    fbgrab fbida fontforge fvwm glm gphoto2 libgphoto2 \
    gtkperf jasper leptonica libmng libsdl2-image libsdl2-mixer libsdl2-net \
    libsdl-gfx libsdl-image libsdl-mixer libsdl-net libsdl-ttf \
    libvncserver libyui libyui-ncurses lxdm numlockx openbox openjpeg \
    packagegroup-fonts-truetype pangomm qrencode takao-fonts terminus-font \
    tesseract tesseract-lang tigervnc tslib source-han-sans-cn-fonts \
    source-han-sans-jp-fonts source-han-sans-kr-fonts source-han-sans-tw-fonts ttf-abyssinica \
    libvdpau x11vnc xcursorgen xdotool \
    bdftopcf iceauth sessreg setxkbmap twm xclock xfontsel xgamma xkbevd xkbprint xkbutils \
    xlsatoms xlsclients xlsfonts xmag xmessage xrdb xrefresh xsetmode xsetroot xstdcmap \
    xterm xwd xwud xbitmaps xorg-sgml-doctools \
    font-adobe-100dpi font-adobe-utopia-100dpi \
    font-bh-100dpi font-bh-lucidatypewriter-100dpi font-bitstream-100dpi font-cursor-misc \
    font-misc-misc xorg-fonts-100dpi liblbxutil libxaw libxkbui libxpresent xserver-common \
    ${@bb.utils.contains("DISTRO_FEATURES", "opengl", "freeglut libsdl2-ttf", "", d)} \
    "



RDEPENDS_packagegroup-meta-oe-kernel ="\
    agent-proxy broadcom-bt-firmware cpupower \
    crash ipmitool minicoredumper oprofile \
    "
RDEPENDS_packagegroup-meta-oe-kernel_append_x86 = " pm-graph "
RDEPENDS_packagegroup-meta-oe-kernel_append_x86-64 = " pm-graph "

RDEPENDS_packagegroup-meta-oe-kernel_remove_libc-musl = "crash minicoredumper"

RDEPENDS_packagegroup-meta-oe-kernel_remove_mips64 = "crash"
RDEPENDS_packagegroup-meta-oe-kernel_remove_mips64el = "crash"

RDEPENDS_packagegroup-meta-oe-kernel_remove_riscv64 = "crash oprofile"
RDEPENDS_packagegroup-meta-oe-kernel_remove_riscv32 = "crash oprofile"

RDEPENDS_packagegroup-meta-oe-multimedia ="\
    alsa-oss audiofile cdrkit id3lib \
    a2jmidid jack libass libburn libcdio libcdio-paranoia \
    libdvdread libmms libmodplug libopus live555 \
    mplayer-common opus-tools \
    sound-theme-freedesktop v4l-utils yavta wavpack libvpx \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "xpext pavucontrol xsp", "", d)} \
    libmikmod \
    ${@bb.utils.contains("LICENSE_FLAGS_WHITELIST", "commercial", "libmad faad2 mpv", "", d)} \
    "
RDEPENDS_packagegroup-meta-oe-multimedia_remove_libc-musl = "alsa-oss"

RDEPENDS_packagegroup-meta-oe-navigation ="\
    geoclue geos libspatialite proj \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "orrery", "", d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "bluz4", "gpsd gpsd-machine-conf", "", d)} \
    "

RDEPENDS_packagegroup-meta-oe-security ="\
    tomoyo-tools \
    ${@bb.utils.contains("DISTRO_FEATURES", "pam", "passwdqc", "", d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "bluz5", "nmap", "", d)} \
    "

RDEPENDS_packagegroup-meta-oe-shells ="\
    dash tcsh zsh \
    "

NE10 = ""
NE10_aarch64 = "ne10"
NE10_armv7a = "ne10"
NE10_armv7ve = "ne10"

RDEPENDS_packagegroup-meta-oe-support ="\
    anthy asio atop augeas avro-c bdwgc frame grail c-ares \
    ccid ceres-solver ckermit cpprest ctapi-common daemonize \
    daemontools devmem2 dfu-util dfu-util-native digitemp \
    dstat eject enca epeg espeak fbset fbset-modes \
    fftw fltk-native gd gflags glog gnulib gperftools \
    gpm gradm gsl gsoap hddtemp hidapi htop hunspell hwdata iksemel \
    libinih inotify-tools joe lcms lcov libatasmart libbytesize \
    libcereal libcyusbserial libee libeigen libestr libftdi libgit2 \
    libgpiod libiio libjs-jquery libjs-sizzle liblinebreak libmicrohttpd \
    libmxml liboauth libol liboop libp11 libraw1394 libsmi libsoc libssh2 \
    libssh libtar libteam libtinyxml2 libtinyxml libusbg libusb-compat libutempter \
    links lockdev log4c log4cpp logwarn libdevmapper lvm2 \
    mailcap mbuffer mg minini \
    multipath-tools nano neon nmon numactl onig openct openldap \
    opensc wbxml2 p910nd pcsc-lite picocom libotr pidgin \
    pngcheck poco poppler poppler-data portaudio-v19 pps-tools \
    pv pxaregs raptor2 rdfind read-edid rsnapshot \
    satyr sdparm pty-forward-native serial-forward \
    sg3-utils sharutils spitools srecord ssiapi stm32flash \
    syslog-ng system-config-keyboard tbb thin-provisioning-tools tokyocabinet \
    tree uhubctl unixodbc uriparser usb-modeswitch \
    usb-modeswitch-data uthash utouch-evemu utouch-frame \
    vim vim-tiny websocketpp xdelta3 xdg-user-dirs xmlstarlet \
    zbar zile \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "geis toscoterm uim synergy utouch-mtview links-x11 fltk pidgin-otr", "", d)} \
    libcanberra \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11 pam", "xorgxrdp xrdp", "", d)} \
    procmail \
    ${@bb.utils.contains("DISTRO_FEATURES", "polkit", "udisks2 upower", "", d)} \
    ${NE10} \
"

RDEPENDS_packagegroup-meta-oe-support-python2 ="\
    ${@bb.utils.contains("BBFILE_COLLECTIONS", "meta-python2", "lio-utils", "", d)} \
"

RDEPENDS_packagegroup-meta-oe-support_remove_arm ="numactl"
RDEPENDS_packagegroup-meta-oe-support_remove_mipsarch = "gperftools"
RDEPENDS_packagegroup-meta-oe-support_remove_riscv64 = "gperftools uim"
RDEPENDS_packagegroup-meta-oe-support_remove_riscv32 = "gperftools uim"
RDEPENDS_packagegroup-meta-oe-support_remove_powerpc = "ssiapi"

RDEPENDS_packagegroup-meta-oe-support-egl ="\
    freerdp libnice opencv \
    "

RDEPENDS_packagegroup-meta-oe-test ="\
    catch2 cppunit cunit cxxtest evtest fb-test \
    fwts googletest pm-qa stress-ng testfloat \
    "
RDEPENDS_packagegroup-meta-oe-test_remove_libc-musl = "pm-qa"
RDEPENDS_packagegroup-meta-oe-test_remove_arm = "fwts"
RDEPENDS_packagegroup-meta-oe-test_remove_mipsarch = "fwts"
RDEPENDS_packagegroup-meta-oe-test_remove_powerpc = "fwts"
RDEPENDS_packagegroup-meta-oe-test_remove_riscv64 = "fwts"
RDEPENDS_packagegroup-meta-oe-test_remove_riscv32 = "fwts"

RDEPENDS_packagegroup-meta-oe-ptest-packages = "\
    zeromq-ptest \
    leveldb-ptest \
    psqlodbc-ptest \
    lua-ptest \
    protobuf-ptest \
    rsyslog-ptest \
    oprofile-ptest \
    libteam-ptest \
    uthash-ptest \
    libee-ptest \
    poco-ptest \
    "
RDEPENDS_packagegroup-meta-oe-ptest-packages_remove_riscv64 = "oprofile-ptest"
RDEPENDS_packagegroup-meta-oe-ptest-packages_remove_riscv32 = "oprofile-ptest"

RDEPENDS_packagegroup-meta-oe-ptest-packages_append_x86 = "\
    mcelog-ptest \
"
RDEPENDS_packagegroup-meta-oe-ptest-packages_append_x86-64 = "\
    mcelog-ptest \
"
RDEPENDS_packagegroup-meta-oe-ptest-packages_remove_arm = "numactl-ptest"
