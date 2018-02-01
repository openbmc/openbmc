if [ -x $HOME/.Xsession ]; then
    exec $HOME/.Xsession
elif [ -x /usr/bin/x-session-manager ]; then
    exec /usr/bin/x-session-manager
else
    exec /usr/bin/x-window-manager
fi
