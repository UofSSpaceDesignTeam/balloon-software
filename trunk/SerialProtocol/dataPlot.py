import sys
from PyQt4.QtCore import *
from PyQt4.QtGui import *
import PyQt4.Qwt5 as Qwt
import serial
import serialProtocol

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
		plot.setAxisScale(Qwt.QwtPlot.yLeft, 0, 250, 40)
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
		y = serialProtocol.decode()[2]
		x = self.counter
		self.counter += 1
		
		self.yplot.append(int(y))
		self.xplot.append(int(x))
		
	def UiUpdate(self):
		self.plot.setAxisScale(Qwt.QwtPlot.xBottom, self.xplot[0], max(20, self.xplot[-1]))
		self.curve.setData(self.xplot,self.yplot)
		self.plot.replot()

		
def beginSerial():
	serialProtocol.s_recv.baudrate = 1200
	serialProtocol.s_recv.port = "COM8"
	serialProtocol.s_recv.open()
	print "Serial Started"
	
def main():
	beginSerial()
	app = QApplication(sys.argv)
	form = PlotDataWindow()
	form.show()
	app.exec_()

if __name__ == "__main__":
	main()