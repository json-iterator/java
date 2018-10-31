import java.awt.*;
import java.awt.event.*;
class MyFrame extends Frame implements ActionListener
{	PopupMenu p1;
	MenuItem m1,m2,m3;
	static Frame f;
	MyFrame()
	{	m1=new MenuItem("Cut");
		m2=new MenuItem("Copy");
		m3=new MenuItem("Paste");
		m1.addActionListener(this);
		m2.addActionListener(this);
		m3.addActionListener(this);
		p1=new PopupMenu();
		p1.add(m1);
		p1.add(m2);
		p1.add(m3);
		addMouseListener(new MouseAdapter()
		{	public void mousePressed(MouseEvent me)
			{
				if(me.getButton()==3)
					p1.show(f,me.getX(),me.getY());	
			}	
		}
		);
		add(p1);
		setBounds(0,0,400,400);
		setVisible(true);
	}
	public static void main(String args[])
	{
		f=new MyFrame();
	}
	public void actionPerformed(ActionEvent ae)
	{
		String s1=ae.getActionCommand();
		if(s1.equalsIgnoreCase("cut"))
			System.out.println("cut selected");
		else if(s1.equalsIgnoreCase("copy"))
			System.out.println("copy selected");
		else if(s1.equalsIgnoreCase("paste"))
			System.out.println("paste selected");
	}
}
