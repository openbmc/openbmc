#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

python primport_handler () {
    import bb.event
    if not e.data:
        return

    if isinstance(e, bb.event.ParseCompleted):
        import oe.prservice
        #import all exported AUTOPR values
        imported = oe.prservice.prserv_import_db(e.data)
        if imported is None:
            bb.fatal("import failed!")

        for (version, pkgarch, checksum, value) in imported:
            bb.note("imported (%s,%s,%s,%d)" % (version, pkgarch, checksum, value))
    elif isinstance(e, bb.event.ParseStarted):
        import oe.prservice
        oe.prservice.prserv_check_avail(e.data)
}

addhandler primport_handler
primport_handler[eventmask] = "bb.event.ParseCompleted bb.event.ParseStarted"
