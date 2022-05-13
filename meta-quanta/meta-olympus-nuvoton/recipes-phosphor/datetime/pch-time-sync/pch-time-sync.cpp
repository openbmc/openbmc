/* Copyright 2019 Intel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

#include <time.h>

#include <boost/date_time/posix_time/posix_time.hpp>
#include <chrono>
#include <iostream>
#include <phosphor-logging/log.hpp>
#include <sdbusplus/asio/object_server.hpp>
extern "C" {
#include <i2c/smbus.h>
#include <linux/i2c-dev.h>
}

static constexpr uint32_t syncIntervalNormalMS = 60000;
static constexpr uint32_t syncIntervalFastMS = (syncIntervalNormalMS / 2);

static uint32_t syncIntervalMS = syncIntervalNormalMS;

// will update bmc time if the time difference beyond this value
static constexpr uint8_t timeDiffAllowedSecond = 1;

static inline uint8_t bcd2Decimal(uint8_t hex)
{
    uint8_t dec = ((hex & 0xF0) >> 4) * 10 + (hex & 0x0F);
    return dec;
}

class I2CFile
{
  private:
    int fd = -1;

  public:
    I2CFile(const int& i2cBus, const int& slaveAddr, const int& flags)
    {
        std::string i2cDev = "/dev/i2c-" + std::to_string(i2cBus);

        fd = open(i2cDev.c_str(), flags);
        if (fd < 0)
        {
            throw std::runtime_error("Unable to open i2c device.");
        }

        if (ioctl(fd, I2C_SLAVE_FORCE, slaveAddr) < 0)
        {
            close(fd);
            fd = -1;
            throw std::runtime_error("Unable to set i2c slave address.");
        }
    }

    uint8_t i2cReadByteData(const uint8_t& offset)
    {
        int ret = i2c_smbus_read_byte_data(fd, offset);

        if (ret < 0)
        {
            throw std::runtime_error("i2c read failed");
        }
        return static_cast<uint8_t>(ret);
    }

    ~I2CFile()
    {
        if (!(fd < 0))
        {
            close(fd);
        }
    }
};

class PCHSync
{
  private:
    bool getPCHDate(uint8_t& year, uint8_t& month, uint8_t& day, uint8_t& hour,
                    uint8_t& minute, uint8_t& second)
    {
        try
        {
            constexpr uint8_t pchDevI2CBusNumber = 0x04;
            constexpr uint8_t pchDevI2CSlaveAddress = 0x44;
            constexpr uint8_t pchDevRegRTCYear = 0x0f;
            constexpr uint8_t pchDevRegRTCMonth = 0x0e;
            constexpr uint8_t pchDevRegRTCDay = 0x0d;
            constexpr uint8_t pchDevRegRTCHour = 0x0b;
            constexpr uint8_t pchDevRegRTCMinute = 0x0a;
            constexpr uint8_t pchDevRegRTCSecond = 0x09;
            I2CFile pchDev(pchDevI2CBusNumber, pchDevI2CSlaveAddress,
                           O_RDWR | O_CLOEXEC);
            year = pchDev.i2cReadByteData(pchDevRegRTCYear);
            year = bcd2Decimal(year);
            if (year > 99)
            {
                return false;
            }

            month = pchDev.i2cReadByteData(pchDevRegRTCMonth);
            month = bcd2Decimal(month);
            if ((month < 1) || (month > 12))
            {
                return false;
            }

            day = pchDev.i2cReadByteData(pchDevRegRTCDay);
            day = bcd2Decimal(day);
            if ((day < 1) || (day > 31))
            {
                return false;
            }

            hour = pchDev.i2cReadByteData(pchDevRegRTCHour);
            hour = bcd2Decimal(hour);
            if (hour >= 24)
            {
                return false;
            }

            minute = pchDev.i2cReadByteData(pchDevRegRTCMinute);
            minute = bcd2Decimal(minute);
            if (minute >= 60)
            {
                return false;
            }

            second = pchDev.i2cReadByteData(pchDevRegRTCSecond);
            second = bcd2Decimal(second);
            if (second >= 60)
            {
                return false;
            }
        }
        catch (const std::exception& e)
        {
            return false;
        }

        return true;
    }

    bool getSystemTime(time_t& timeSeconds)
    {
        struct timespec sTime = {0};
        int ret = 0;

        ret = clock_gettime(CLOCK_REALTIME, &sTime);

        if (ret != 0)
        {
            return false;
        }
        timeSeconds = sTime.tv_sec;
        return true;
    }

    bool setSystemTime(uint32_t timeSeconds)
    {
        struct timespec sTime = {0};
        int ret = 0;

        sTime.tv_sec = timeSeconds;
        sTime.tv_nsec = 0;

        ret = clock_settime(CLOCK_REALTIME, &sTime);

        return (ret == 0);
    }

    bool updateBMCTime()
    {
        int ret = 0;
        time_t BMCTimeSeconds = 0;
        time_t PCHTimeSeconds = 0;
        struct tm tm = {0};

        // get PCH and system time
        if (!getPCHDate(year, month, day, hour, minute, second))
        {
            return false;
        };

        if (!getSystemTime(BMCTimeSeconds))
        {
            return false;
        }

        std::string dateString =
            "20" + std::to_string(year) + "-" + std::to_string(month) + "-" +
            std::to_string(day) + " " + std::to_string(hour) + ":" +
            std::to_string(minute) + ":" + std::to_string(second);

        strptime(dateString.c_str(), "%Y-%m-%d %H:%M:%S", &tm);

        PCHTimeSeconds = mktime(&tm);
        if (PCHTimeSeconds == -1)
        {
            return false;
        }

        if (std::abs(PCHTimeSeconds - BMCTimeSeconds) > timeDiffAllowedSecond)
        {
            if (!setSystemTime(PCHTimeSeconds))
            {
                return false;
            }
            std::cout << "Update BMC time to " << dateString << std::endl;
        }

        return true;
    }

    void startSyncTimer()
    {
        if (updateBMCTime())
        {
            syncIntervalMS = syncIntervalNormalMS;
        }
        else
        {
            std::cout << "Update BMC time Fail" << std::endl;
            syncIntervalMS = syncIntervalFastMS;
        }

        syncTimer->expires_after(std::chrono::milliseconds(syncIntervalMS));
        syncTimer->async_wait(
            [this](const boost::system::error_code& ec) { startSyncTimer(); });
    }

    std::unique_ptr<boost::asio::steady_timer> syncTimer;
    uint8_t year, month, day, hour, minute, second;

  public:
    PCHSync(boost::asio::io_service& io)
    {
        syncTimer = std::make_unique<boost::asio::steady_timer>(io);
        startSyncTimer();
    }

    ~PCHSync() = default;
};

int main(int argc, char** argv)
{
    boost::asio::io_service io;
    PCHSync pchSyncer(io);

    phosphor::logging::log<phosphor::logging::level::INFO>(
        "Starting PCH time sync service");

    io.run();
    return 0;
}
