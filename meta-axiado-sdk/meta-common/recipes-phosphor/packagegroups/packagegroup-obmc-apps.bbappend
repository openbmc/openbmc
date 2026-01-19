RDEPENDS:${PN}-network += " ax-net iptables iproute2 net-tools"
RDEPENDS:${PN}-extras += "${@ 'anx7688-axiado' if d.getVar('SOC_REVISION') == 'revA' else '' } \
                          axiado-eip-firmware \
                          boot-state \
                          entity-manager \
                          klogmgr \
                          sysproxy \
                          tcu-reset \
                          tdfu \
                         "
