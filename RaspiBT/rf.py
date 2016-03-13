// This file will be placed within the Raspberry Pi with Bluez module installed.
// Through the rf file, raspberry pi will be openly listening to other devices with similar uuid.


import bluetooth
import subprocess
import time

server_sock=bluetooth.BluetoothSocket( bluetooth.RFCOMM )

uuid = "832e4374-95a3-41ac-ad85-f2312cca9c7a"

port = 0
server_sock.bind(("",port))
server_sock.listen(0)

bluetooth.advertise_service( server_sock, "service", uuid )

client_sock,address = server_sock.accept()
print "Accepted connection from ",address
vic = False

// Once connection is established, RSSI signal will be sent via the command of hcitool rssi [mac address]
// If the signal (dec) reaches a strong enough signal i.e. close to 0 (>= -10), socket will send a character to client.
// Client will receive the character via decimal code, which will eventually be used to create events like point system, etc.

while (not vic):
    signal=subprocess.check_output(["hcitool","rssi",address[0]])
    lis = signal.split(": ")
    print "done"
    if lis[1][0] == '0':
        dec = 0
    else:
        lisb = lis[1][1:-1]
        dec = int(lisb)
        print dec
    if(dec <= 10):
        vic = True
        print dec
    time.sleep(5)

print "you are in vicinity"
client_sock.send('a')

client_sock.close()
server_sock.close()
