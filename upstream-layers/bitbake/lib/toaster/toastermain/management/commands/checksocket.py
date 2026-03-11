#!/usr/bin/env python3
#
# BitBake Toaster Implementation
#
# Copyright (C) 2015 Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0-only
#

"""Custom management command checksocket."""

import errno
import socket

from django.core.management.base import BaseCommand, CommandError
from django.utils.encoding import force_str

DEFAULT_ADDRPORT = "0.0.0.0:8000"

class Command(BaseCommand):
    """Custom management command."""

    help = 'Check if Toaster can listen on address:port'

    def add_arguments(self, parser):
        parser.add_argument('addrport', nargs='?', default=DEFAULT_ADDRPORT,
                            help='ipaddr:port to check, %s by default' % \
                                 DEFAULT_ADDRPORT)

    def handle(self, *args, **options):
        addrport = options['addrport']
        if ':' not in addrport:
            raise CommandError('Invalid addr:port specified: %s' % addrport)
        splitted = addrport.split(':')
        try:
            splitted[1] = int(splitted[1])
        except ValueError:
            raise CommandError('Invalid port specified: %s' % splitted[1])
        self.stdout.write('Check if toaster can listen on %s' % addrport)
        try:
            sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
            sock.bind(tuple(splitted))
        except (socket.error, OverflowError) as err:
            errors = {
                errno.EACCES: 'You don\'t have permission to access port %s' \
                              % splitted[1],
                errno.EADDRINUSE: 'Port %s is already in use' % splitted[1],
                errno.EADDRNOTAVAIL: 'IP address can\'t be assigned to',
            }
            if hasattr(err, 'errno') and err.errno in errors:
                errtext = errors[err.errno]
            else:
                errtext = force_str(err)
            raise CommandError(errtext)

        self.stdout.write("OK")
