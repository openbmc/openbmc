FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

IPMB_CHANNELS ?= ""

python do_ipmb_channels() {
    import json
    channels = []

    for channel in d.getVar('IPMB_CHANNELS').split():
        channels.append({
            "type": "ipmb",
            "slave-path": channel,
            "bmc-addr": 32,
            "remote-addr": 64,
            "devIndex": len(channels)
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
