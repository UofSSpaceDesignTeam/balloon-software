import sys
from PyQt4.QtCore import *
from PyQt4.QtGui import *
import PyQt4.Qwt5 as Qwt
import serial
import time

class PlotDataWindow(QMainWindow):
	def __init__(self, parent=None):
		super(PlotDataWindow, self).__init__(parent)
		
		self.create_main_frame()
		self.uitimer = QTimer()
		self.stimer = QTimer()
		QObject.connect(self.uitimer, SIGNAL("timeout()"), self.UiUpdate)
		QObject.connect(self.stimer, SIGNAL("timeout()"), self.SerialUpdate)
		self.uitimer.start(500)
		self.stimer.start(100)
		self.counter = 1
		self.xplot = [int(0)]
		self.yplot = [int(0)]
		self.gpsAlt = "0"
		
		self.recvSerial = serial.Serial("COM3", 1200, timeout=1)
		self.sendSerial = serial.Serial("COM8", 9600, timeout=1)
		print "Serial Started"
		
		self.create_status_bar(self.gpsAlt)

	def make_data_box(self, name):
		label = QLabel(name)
		qle = QLineEdit()
		qle.setEnabled(False)
		qle.setFrame(False)
		return (label, qle)
		
	def create_plot(self):
		plot = Qwt.QwtPlot(self)
		plot.setCanvasBackground(Qt.black)
		plot.setAxisTitle(Qwt.QwtPlot.xBottom, 'Time')
		plot.setAxisScale(Qwt.QwtPlot.xBottom, 0, 10, 1)
		plot.setAxisTitle(Qwt.QwtPlot.yLeft, 'Temperature')
		plot.setAxisScale(Qwt.QwtPlot.yLeft, -60, 60, 5)
		plot.replot()

		curve = Qwt.QwtPlotCurve('')
		curve.setRenderHint(Qwt.QwtPlotItem.RenderAntialiased)
		pen = QPen(QColor('limegreen'))
		pen.setWidth(2)
		curve.setPen(pen)
		curve.attach(plot)
		return plot, curve
		
	def create_main_frame(self):
		self.plot, self.curve = self.create_plot()

		plot_layout = QVBoxLayout()
		plot_layout.addWidget(self.plot)

		plot_groupbox = QGroupBox('Temperature')
		plot_groupbox.setLayout(plot_layout)

		# Main frame and layout
		self.main_frame = QWidget()
		main_layout = QVBoxLayout()
		main_layout.addWidget(plot_groupbox)
		main_layout.addStretch(1)
		self.main_frame.setLayout(main_layout)

		self.setCentralWidget(self.main_frame)
		
	def SerialUpdate(self):
		try:
			recv = self.recvSerial.readline()
			if recv[0] == "G":
				template = "$GPGGA,123519,4807.038,N,01131.000,W,1,08,0.9,545.4,M,46.9,M,,*47"
				utcTime = time.strftime("%H%M%S", time.gmtime())
				gpsLonDeg = (int(recv[1:6]))/1000
				gpsLonMin = (float(int(recv[1:6]))/1000 - gpsLonDeg) * 60
				if gpsLonMin < 10:
					gpsLonMin = "0" + str(gpsLonMin)
				else: gpsLonMin = str(gpsLonMin)
				gpsLon = str(gpsLonDeg) + gpsLonMin
				gpsLatDeg = (int(recv[8:14]))/1000
				gpsLatMin = (float(int(recv[8:14]))/1000 - gpsLatDeg) * 60
				if gpsLatMin < 10:
					gpsLatMin = "0" + str(gpsLatMin)
				else: gpsLatMin = str(gpsLatMin)
				gpsLat = str(gpsLatDeg) + gpsLatMin
				self.gpsAlt = str(float(int(recv[16:20]))/10)
				
				sentence = "GPGGA," + utcTime + "," + gpsLon + ",N," + gpsLat + ",W," + "1,08,0.9," + self.gpsAlt + ",M," + "46.9,M,," 
				checksentence = list(bytearray(sentence))
				check = 0
				for n in range(len(checksentence)):
					check = check ^ checksentence[n]
				
				check = "%x" % check
				sentence = "$" + sentence + "*" + check + "\r\n"
				
				#print sentence
				self.sendSerial.write(sentence)
				
				self.status_text.setText("Current Altitude: " + self.gpsAlt + "m")
				
			if recv[0] == "T":
				recv = recv[1:]
				y = recv.partition(',')[0]
				print y
				
				x = self.counter
				self.counter += 1
				
				try:
					self.yplot.append(int(y))
					self.xplot.append(int(x))
				except:
					raise
				
		except:
			pass
			
	def create_status_bar(self, alt):
		self.status_text = QLabel(alt)
		self.statusBar().addWidget(self.status_text, 1)
		
	def UiUpdate(self):
		self.plot.setAxisScale(Qwt.QwtPlot.xBottom, self.xplot[0], max(20, self.xplot[-1]))
		self.curve.setData(self.xplot,self.yplot)
		self.plot.replot()

	
def main():
	app = QApplication(sys.argv)
	form = PlotDataWindow()
	form.show()
	app.exec_()

if __name__ == "__main__":
	main()