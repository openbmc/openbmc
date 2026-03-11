# SPDX-FileCopyrightText: Andrei Gherzan <andrei.gherzan@huawei.com>
#
# SPDX-License-Identifier: MIT

FROM christophebedard/dco-check:latest

# Run under normal user called 'ci'
RUN useradd --create-home --uid 1000 --shell /usr/bin/bash ci
USER ci

COPY ./dco-check/entrypoint.sh /
COPY ./utils.sh /
ENTRYPOINT ["/entrypoint.sh"]
