package com.mannu;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;

import javax.swing.border.EtchedBorder;
import java.awt.Color;
import java.awt.Image;
import java.awt.List;

import javax.swing.JTextField;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.security.interfaces.RSAKey;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;

public class MainForm extends Thread{
	private Connection connection;
	private String usname;
	JLabel imglbl;
	private JTextField jid;
	private JTextField bookno;
	private JTextField villnam;
	private JTextField docname;
	private JTextField totpag;
	private JTextField currpag;
	private JTextField nwpag;
	ArrayList<Records> records;
	int cpage;
	int arcou;
	private JTextField fnam;
	
	public MainForm(Connection connection, String upperCase) {
		this.connection=connection;
		this.usname=upperCase;
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	public void run() {
		
		JFrame frame=new JFrame("WELCOME "+usname.toUpperCase());
		frame.setSize(1109, 688);
		frame.getContentPane().setLayout(null);
		
		imglbl = new JLabel("");
		imglbl.setBounds(365, 0, 728, 649);
		frame.getContentPane().add(imglbl);
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Data Control", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel.setBounds(10, 11, 345, 219);
		panel.setLayout(null);
		frame.getContentPane().add(panel);
		
		JLabel lblJid = new JLabel("Jid:");
		lblJid.setBounds(10, 25, 36, 14);
		panel.add(lblJid);
		
		jid = new JTextField();
		jid.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent event) {
				char vchar = event.getKeyChar();
		        if ((!Character.isDigit(vchar)) || (vchar == '\b') || (vchar == '') || 
		          (bookno.getText().length() == 4)) {
		          event.consume();
		        }
			}
		});
		jid.setBounds(45, 22, 59, 20);
		panel.add(jid);
		jid.setColumns(10);
		
		bookno = new JTextField();
		bookno.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent event) {
				char vchar = event.getKeyChar();
		        if ((!Character.isDigit(vchar)) || (vchar == '\b') || (vchar == '') || 
		          (bookno.getText().length() == 4)) {
		          event.consume();
		        }
			}
		});
		bookno.setColumns(10);
		bookno.setBounds(172, 22, 59, 20);
		panel.add(bookno);
		
		JLabel lblBookNo = new JLabel("Book No:");
		lblBookNo.setBounds(114, 25, 59, 14);
		panel.add(lblBookNo);
		
		JButton btnSubmit = new JButton("Submit");
		btnSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(jid.getText().isEmpty()) {
					JOptionPane.showMessageDialog(null, "Enter jid");
					jid.requestFocus();
				} else if(bookno.getText().isEmpty()) {
					JOptionPane.showMessageDialog(null, "Enter Book No");
					bookno.requestFocus();
				} else {
					File tmp=new File("C:\\tmp\\img");
					try {
						FileUtils.deleteDirectory(new File(tmp.getAbsolutePath()));
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					records=new ArrayList<Records>();
					try {
						PreparedStatement ps=connection.prepareStatement("select distinct m.etitle,doc.edesc,d.actualpages,i.pageno,concat(s.imgpath,i.repository,'\\',i.filename) from tbimagetrans i inner join tbjacketd d on i.jid=d.jid and i.bookno=d.bookno " + 
								"inner join tbsysparams s on i.cid=s.cid inner join tbmasterdata m on i.villageid=m.id inner join tbdocumentm doc on i.docid=doc.docid " + 
								"and i.ofcid=doc.ofcid where i.jid="+jid.getText()+" and i.bookno="+bookno.getText()+" order by i.pageno");
						ResultSet rs=ps.executeQuery();
						while (rs.next()) {
							Records rec=new Records();
							rec.setVillage(rs.getString(1));
							rec.setDocname(rs.getString(2));
							rec.setAcpages(rs.getString(3));
							rec.setCpage(rs.getString(4));
							rec.setFpath(rs.getString(5));
							records.add(rec);
						}
						ps.close();
						rs.close();
						
						System.out.println("Arrey: "+records.size());
						cpage=0;
						dataupdate(cpage);
						
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
			}
		});
		btnSubmit.setBounds(241, 21, 89, 23);
		panel.add(btnSubmit);
		
		JLabel lblVillageName = new JLabel("Village Name:");
		lblVillageName.setBounds(34, 55, 83, 14);
		panel.add(lblVillageName);
		
		villnam = new JTextField();
		villnam.setForeground(Color.BLUE);
		villnam.setEditable(false);
		villnam.setBounds(116, 52, 192, 20);
		panel.add(villnam);
		villnam.setColumns(10);
		
		JLabel lblDocName = new JLabel("Doc Name:");
		lblDocName.setBounds(34, 83, 83, 14);
		panel.add(lblDocName);
		
		docname = new JTextField();
		docname.setForeground(Color.BLUE);
		docname.setEditable(false);
		docname.setColumns(10);
		docname.setBounds(116, 80, 192, 20);
		panel.add(docname);
		
		totpag = new JTextField();
		totpag.setForeground(Color.BLUE);
		totpag.setEditable(false);
		totpag.setColumns(10);
		totpag.setBounds(96, 108, 59, 20);
		panel.add(totpag);
		
		JLabel lblTotalPage = new JLabel("Total Page:");
		lblTotalPage.setBounds(22, 111, 83, 14);
		panel.add(lblTotalPage);
		
		currpag = new JTextField();
		currpag.setForeground(Color.BLUE);
		currpag.setEditable(false);
		currpag.setColumns(10);
		currpag.setBounds(254, 108, 59, 20);
		panel.add(currpag);
		
		JLabel lblCurrentPage = new JLabel("Current Page:");
		lblCurrentPage.setBounds(169, 111, 83, 14);
		panel.add(lblCurrentPage);
		
		JLabel lblNewPageNo = new JLabel("New Page No:");
		lblNewPageNo.setBounds(29, 142, 83, 14);
		panel.add(lblNewPageNo);
		
		nwpag = new JTextField();
		nwpag.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent event) {
				char vchar = event.getKeyChar();
		        if ((!Character.isDigit(vchar)) || (vchar == '\b') || (vchar == '') || 
		          (bookno.getText().length() == 4)) {
		          event.consume();
		        }
			}
		});
		nwpag.setForeground(Color.BLUE);
		nwpag.setColumns(10);
		nwpag.setBounds(113, 139, 59, 20);
		panel.add(nwpag);
		
		JButton btnSubmit_1 = new JButton("Save");
		btnSubmit_1.setMnemonic(KeyEvent.VK_S);
		btnSubmit_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(totpag.getText().isEmpty()) {
					JOptionPane.showMessageDialog(null, "data not available");
				} else {
					if(nwpag.getText().isEmpty()) {
						JOptionPane.showMessageDialog(null, "Enter new page no");
					} else {
						try {
							int dialogButton = JOptionPane.YES_NO_OPTION;
							JOptionPane.showConfirmDialog (null, "Do you want to update?","Warning",dialogButton);
							if(dialogButton==JOptionPane.YES_OPTION) {
								PreparedStatement ps=connection.prepareStatement("select distinct filename from tbimagetrans where jid='"+jid.getText()+"' and bookno='"+bookno.getText()+"' and pageno='"+nwpag.getText()+"'");
								ResultSet rs=ps.executeQuery();
								if(rs.next()) {
									JOptionPane.showConfirmDialog (null, "Page no already exists. Do you want to change ?","Warning",dialogButton);
									if(dialogButton==JOptionPane.YES_OPTION) {
										try {
											PreparedStatement ch=connection.prepareStatement("select max(pageno) from tbimagetrans where jid="+jid.getText()+" and bookno="+bookno.getText());
											ResultSet chr=ch.executeQuery();
											if(chr.next()) {
												int maxp=chr.getInt(1)+1;
												PreparedStatement updat=connection.prepareStatement("update tbimagetrans set pageno="+maxp+" where filename='"+rs.getString(1)+"' and jid="+jid.getText()+" and bookno="+bookno.getText());
												updat.execute();
												PreparedStatement updat1=connection.prepareStatement("update tbtransactions set pageno="+maxp+" where filename='"+rs.getString(1)+"' and jid="+jid.getText()+" and bookno="+bookno.getText());
												updat1.execute();
												updat.close();
												updat1.close();
												
												PreparedStatement npimg=connection.prepareStatement("update tbimagetrans set pageno="+nwpag.getText()+" where filename='"+fnam.getText()+"' and jid="+jid.getText()+" and bookno="+bookno.getText());
												npimg.execute();
												PreparedStatement nptrn=connection.prepareStatement("update tbtransactions set pageno="+nwpag.getText()+" where filename='"+fnam.getText()+"' and jid="+jid.getText()+" and bookno="+bookno.getText());
												nptrn.execute();
												npimg.close();
												nptrn.close();
												
												records.clear();
												records=new ArrayList<Records>();
												
													PreparedStatement pss=connection.prepareStatement("select distinct m.etitle,doc.edesc,d.actualpages,i.pageno,concat(s.imgpath,i.repository,'\\',i.filename) from tbimagetrans i inner join tbjacketd d on i.jid=d.jid and i.bookno=d.bookno " + 
															"inner join tbsysparams s on i.cid=s.cid inner join tbmasterdata m on i.villageid=m.id inner join tbdocumentm doc on i.docid=doc.docid " + 
															"and i.ofcid=doc.ofcid where i.jid="+jid.getText()+" and i.bookno="+bookno.getText()+" order by i.pageno");
													ResultSet rss=pss.executeQuery();
													while (rss.next()) {
														Records rec=new Records();
														rec.setVillage(rss.getString(1));
														rec.setDocname(rss.getString(2));
														rec.setAcpages(rss.getString(3));
														rec.setCpage(rss.getString(4));
														rec.setFpath(rss.getString(5));
														records.add(rec);
													}
													pss.close();
													rss.close();
												dataupdate(arcou);
												JOptionPane.showMessageDialog(null, "Done");
												nwpag.setText("");
											}
											ch.close();
											chr.close();
										} catch (Exception e) {
											e.printStackTrace();
										}
									} else {
										System.out.println("No update");
									}
									
								} else {
									PreparedStatement updat=connection.prepareStatement("update tbimagetrans set pageno="+nwpag.getText()+" where filename='"+fnam.getText()+"' and jid="+jid.getText()+" and bookno="+bookno.getText());
									updat.execute();
									PreparedStatement updat1=connection.prepareStatement("update tbtransactions set pageno="+nwpag.getText()+" where filename='"+fnam.getText()+"' and jid="+jid.getText()+" and bookno="+bookno.getText());
									updat1.execute();
									updat.close();
									updat1.close();
								}
							} else {
								System.out.println("No option");
							}
							
						} catch (Exception e) {
							JOptionPane.showMessageDialog(null, "Error: "+e);
						}
					}
				}
			}
		});
		btnSubmit_1.setBounds(193, 138, 89, 23);
		panel.add(btnSubmit_1);
		
		JButton btnNext = new JButton("Previous");
		btnNext.setMnemonic(KeyEvent.VK_P);
		btnNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(totpag.getText().isEmpty()) {
					JOptionPane.showMessageDialog(null, "data not available");
				} else {
					if(arcou==0) {
						JOptionPane.showMessageDialog(null, "This is first page");
					} else {
						--arcou;
						dataupdate(arcou);
					}
				}
			}
		});
		btnNext.setBounds(25, 173, 89, 23);
		panel.add(btnNext);
		
		JButton button = new JButton("Next");
		button.setMnemonic(KeyEvent.VK_N);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(totpag.getText().isEmpty()) {
					JOptionPane.showMessageDialog(null, "data not available");
				} else {
					if(arcou==Integer.parseInt(totpag.getText())) {
						JOptionPane.showMessageDialog(null, "This is last page");
					} else {
						arcou=1+arcou;
						dataupdate(arcou);
					}
				}
			}
		});
		button.setBounds(127, 173, 89, 23);
		panel.add(button);
		
		JButton btnClose = new JButton("Close");
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		btnClose.setBounds(232, 173, 89, 23);
		panel.add(btnClose);
		
		JLabel lblShortCutKey = new JLabel("Shortcut Keys");
		lblShortCutKey.setFont(new Font("Century", Font.BOLD, 12));
		lblShortCutKey.setBounds(41, 305, 97, 14);
		frame.getContentPane().add(lblShortCutKey);
		
		JLabel lblForNextAntn = new JLabel("For Next:    Alt + N");
		lblForNextAntn.setBounds(51, 330, 104, 14);
		frame.getContentPane().add(lblForNextAntn);
		
		JLabel lblForPreviousAntn = new JLabel("For Previous :   Alt + P");
		lblForPreviousAntn.setBounds(53, 355, 132, 14);
		frame.getContentPane().add(lblForPreviousAntn);
		
		JLabel lblForSave = new JLabel("For Save :   Alt + S");
		lblForSave.setBounds(53, 379, 132, 14);
		frame.getContentPane().add(lblForSave);
		
		JLabel lblFileName = new JLabel("File Name:");
		lblFileName.setBounds(19, 238, 74, 14);
		frame.getContentPane().add(lblFileName);
		
		fnam = new JTextField();
		fnam.setForeground(Color.BLUE);
		fnam.setEditable(false);
		fnam.setColumns(10);
		fnam.setBounds(93, 235, 192, 20);
		frame.getContentPane().add(fnam);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	protected void dataupdate(int cpage2) {
		arcou=cpage2;
		File tmp=new File("C:\\tmp\\img");
		if(!tmp.exists()) {
			tmp.mkdirs();
		}
		
		System.out.println("Data Records: "+cpage2+" || Arrey: "+records.size());
		Records rrc=records.get(cpage2);
		villnam.setText(rrc.getVillage());
		docname.setText(rrc.getDocname());
		totpag.setText(rrc.getAcpages());
		currpag.setText(rrc.getCpage());
		File pp=new File(rrc.getFpath());
		fnam.setText(pp.getName());
		System.out.println("DD: "+pp);
		
		try {
			PDDocument document = PDDocument.load(new File(rrc.getFpath()));
			PDFRenderer pdfRenderer = new PDFRenderer(document);
			BufferedImage bim = pdfRenderer.renderImageWithDPI(0,25, ImageType.RGB);
			ImageIOUtil.writeImage(bim,tmp+"\\"+pp.getName()+".png",0);
			ImageIcon ic=new ImageIcon(tmp+"\\"+pp.getName()+".png");
			Image ii=ic.getImage();
			Image image=ii.getScaledInstance(imglbl.getWidth(), imglbl.getHeight(), Image.SCALE_SMOOTH);
			imglbl.setIcon(new ImageIcon(image));
			document.close();
			
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error: "+e);
		}
			
	}
	
}
