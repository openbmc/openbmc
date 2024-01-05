FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

IPMB_CHANNELS ?= ""
IPMB_REMOTE_ADDR ?= ""

python do_ipmb_channels() {
    import json
    channels = []
    ipmb_channel = d.getVar('IPMB_CHANNELS').split()
    ipmb_remote_addr = d.getVar('IPMB_REMOTE_ADDR').split()

    for i in range(len(ipmb_channel)):
        channels.append({
            "type": "ipmb",
            "slave-path": ipmb_channel[i],
            "bmc-addr": 32,
            "remote-addr": int(ipmb_remote_addr[i]) if i < len(ipmb_remote_addr) else 64,
            "devIndex": i
        })

    data = { "channels" : channels }

    path = os.path.join(d.getVar('D') + d.getVar('datadir'),
                        'ipmbbridge', 'ipmb-channels.json')

    with open(path, 'w') as f:
        json.dump(data, f, indent=4)
}
do_ipmb_channels[vardeps] += "IPMB_CHANNELS"
addtask ipmb_channels after do_install before do_package

do_install:append() {
    install -d ${D}${datadir}/ipmbbridge
}
