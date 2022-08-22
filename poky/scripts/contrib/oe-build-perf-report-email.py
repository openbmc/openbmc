#!/usr/bin/python3
#
# Send build performance test report emails
#
# Copyright (c) 2017, Intel Corporation.
#
# SPDX-License-Identifier: GPL-2.0-only
#

import argparse
import base64
import logging
import os
import pwd
import re
import shutil
import smtplib
import socket
import subprocess
import sys
import tempfile
from email.mime.text import MIMEText


# Setup logging
logging.basicConfig(level=logging.INFO, format="%(levelname)s: %(message)s")
log = logging.getLogger('oe-build-perf-report')


def parse_args(argv):
    """Parse command line arguments"""
    description = """Email build perf test report"""
    parser = argparse.ArgumentParser(
        formatter_class=argparse.ArgumentDefaultsHelpFormatter,
        description=description)

    parser.add_argument('--debug', '-d', action='store_true',
                        help="Verbose logging")
    parser.add_argument('--quiet', '-q', action='store_true',
                        help="Only print errors")
    parser.add_argument('--to', action='append',
                        help="Recipients of the email")
    parser.add_argument('--cc', action='append',
                        help="Carbon copy recipients of the email")
    parser.add_argument('--bcc', action='append',
                        help="Blind carbon copy recipients of the email")
    parser.add_argument('--subject', default="Yocto build perf test report",
                        help="Email subject")
    parser.add_argument('--outdir', '-o',
                        help="Store files in OUTDIR. Can be used to preserve "
                             "the email parts")
    parser.add_argument('--text',
                        help="Plain text message")

    args = parser.parse_args(argv)

    if not args.text:
        parser.error("Please specify --text")

    return args


def send_email(text_fn, subject, recipients, copy=[], blind_copy=[]):
    # Generate email message
    with open(text_fn) as f:
        msg = MIMEText("Yocto build performance test report.\n" + f.read(), 'plain')

    pw_data = pwd.getpwuid(os.getuid())
    full_name = pw_data.pw_gecos.split(',')[0]
    email = os.environ.get('EMAIL',
                           '{}@{}'.format(pw_data.pw_name, socket.getfqdn()))
    msg['From'] = "{} <{}>".format(full_name, email)
    msg['To'] = ', '.join(recipients)
    if copy:
        msg['Cc'] = ', '.join(copy)
    if blind_copy:
        msg['Bcc'] = ', '.join(blind_copy)
    msg['Subject'] = subject

    # Send email
    with smtplib.SMTP('localhost') as smtp:
        smtp.send_message(msg)


def main(argv=None):
    """Script entry point"""
    args = parse_args(argv)
    if args.quiet:
        log.setLevel(logging.ERROR)
    if args.debug:
        log.setLevel(logging.DEBUG)

    if args.outdir:
        outdir = args.outdir
        if not os.path.exists(outdir):
            os.mkdir(outdir)
    else:
        outdir = tempfile.mkdtemp(dir='.')

    try:
        log.debug("Storing email parts in %s", outdir)
        if args.to:
            log.info("Sending email to %s", ', '.join(args.to))
            if args.cc:
                log.info("Copying to %s", ', '.join(args.cc))
            if args.bcc:
                log.info("Blind copying to %s", ', '.join(args.bcc))
            send_email(args.text, args.subject, args.to, args.cc, args.bcc)
    except subprocess.CalledProcessError as err:
        log.error("%s, with output:\n%s", str(err), err.output.decode())
        return 1
    finally:
        if not args.outdir:
            log.debug("Wiping %s", outdir)
            shutil.rmtree(outdir)

    return 0


if __name__ == "__main__":
    sys.exit(main())
