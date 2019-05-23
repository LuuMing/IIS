/*
 *=============================================================================

 *=============================================================================
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <pthread.h>
#include <errno.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/wait.h>
//
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <ctype.h>
#include <sys/socket.h>
#include <sys/stat.h>
#include <netinet/ip.h>
#include <fcntl.h>
#include <errno.h>
#include <net/if.h>
#include <string.h>
#include <sys/types.h>
#include <sys/ioctl.h>
#include <sys/io.h>
#include <linux/rtc.h>
#include <time.h>

#include "bsp.h"

/*
#define GPM2 S3C6410_PIO_PM2
#define GPL1 S3C6410_PIO_PL1
#define GPK2 S3C6410_PIO_PK2
#define GPK8 S3C6410_PIO_PK8
#define GPM2 S3C6410_PIO_PM2
#define GPI0 B0
#define GPI8 G0
#define IY2 S3C6410_PIO_PF7
#define IY0 S3C6410_PIO_PF5
#define XDACOUT1 IY2
#define ADC1 IY0
//irDA_OUT
#define irDA_OUT XEINT14

//telecontrol
#define wirelessSW1 XEINT15
#define wirelessSW2 XEINT13
#define wirelessSW3 EINT4
#define wirelessSW4 EINT9

#define   SPI_MOSI      S3C6410_PIO_PC0
#define   SPICLK		S3C6410_PIO_PC1
#define   TLC5615_CS1	S3C6410_PIO_PA2
*/

/////////////////////////I2C��غ���/////////////////////////////
unsigned char I2C_ACK;


//#define I2C_SCL /*S3C2440_PIO_PE13*/S3C6410_PIO_PA6	//RTCʱ����
//#define I2C_SDA /*S3C2440_PIO_PJ6*/S3C6410_PIO_PC4	//RTC������


#define SomeNOP Delay(35)/*uudelay(50)*/;

//0����д�豸��1�������豸
#define SlaveAddrWrite   0xa0 
#define SlaveAddrRead    0xa0+1 

void DelayBy10ms(void)
{
	unsigned int i = 0,j = 0;
	
    for(i=0;i<=35000;i++)
    {
		;
    }
}



int fd_rs232;//
void rs232_open(void)
{
	struct termios tio ;
	//char fd_rs232;
		
	fd_rs232 = open("/dev/ttySAC1", O_RDWR | O_NOCTTY | O_NDELAY | O_NONBLOCK);
	
	if (fd_rs232 < 0)
		{
         printf("open omc port erro: %s",strerror(errno)) ;
         exit (-1);
		}
		
	printf("open com1 sucess!.........\n") ;	
	
    
	 
    /*
     *������9600��8λ����λ����֤
     *���򲻻��Ϊ�˿ڵ�ռ�����ߡ�ʹ�ܶ˿ڶ������������
     */
    memset(&tio, 0, sizeof(tio));/*B19200*/
    tio.c_cflag =  B9600/*B4800*/ | CS8 | CLOCAL | CREAD;
	tio.c_cflag &= ~(CSTOPB | PARENB | CRTSCTS) ;/*1λֹͣλ����У��λ����������*/
    tio.c_iflag = IGNPAR;
    tio.c_oflag  &= ~OPOST;   /*���ģʽ��ԭʼ�������*/
	tio.c_lflag  &= ~(ICANON | ECHO | ECHOE | ISIG);  /*Input*/
    tio.c_cc[VTIME] = 0;  /*�����ַ�����ȡ��һ���ַ��ĵȴ�ʱ��*/
    tio.c_cc[VMIN] = 1;  /*�����ַ�����Ҫ��ȡ�ַ�����С����*/

    tcflush(fd_rs232, TCIFLUSH);  /*��������ݿ��Խ��գ�������*/  
    /*
     *FNDELAY����readʱû�����ݿɶ����������������Ϊ0�����û�����ݾ�����
     */
    fcntl(fd_rs232, F_SETFL, 0/*FNDELAY*/);
    tcsetattr(fd_rs232, TCSANOW, &tio);/*����������TCSANOW�����иı�������Ч��*/
	

}
 
int main(int argc, char *argv[])
{
	char readdata=0;
	char cmd=2,readBuf[100];
	int readCnt=0,i;
	/*
	 * Ӳ����ʼ��
	 */
	bsp_init();
	rs232_open(); //CON3 ---- /dev/ttySAC2
	
	sleep(1);
	write(fd_rs232,&cmd,1);	 // ����ģ�� �����Զ���Ⲣ��ȡ���к�ģʽ
	while(1)
	{
		sleep(1);
		readCnt = read(fd_rs232,readBuf,50);
		if(readCnt>0)
		{
			break;
		}
	}
	
	return 0;
}


/* end of file */
