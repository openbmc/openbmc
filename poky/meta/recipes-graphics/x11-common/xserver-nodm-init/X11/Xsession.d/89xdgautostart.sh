XDGAUTOSTART=/etc/xdg/autostart
if [ -d $XDGAUTOSTART ]; then
    for SCRIPT in $XDGAUTOSTART/*; do
        CMD=`grep ^Exec= $SCRIPT | cut -d '=' -f 2`
        $CMD &
    done
fi
