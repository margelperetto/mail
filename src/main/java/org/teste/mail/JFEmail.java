package org.teste.mail;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import org.teste.mail.PropertiesUtils.Prop;

import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class JFEmail extends JFrame{
	
	private JTextField jtfRemetente = new JTextField();
	private JTextField jtfSmtp = new JTextField();
	private JPasswordField jpfPass = new JPasswordField();
	private JSpinner jsPort = new JSpinner(new SpinnerNumberModel(25, 0, 99999, 1));
	private JCheckBox jcbStartTLS = new JCheckBox("TLS", true);
	private JCheckBox jcbSalvarSenha = new JCheckBox("Salvar senha");
	private JTextField jtfTitulo = new JTextField();
	private JTextField jtfDestinatario = new JTextField();
	private JTextField jtfPathBody = new JTextField();
	private JTextField jtfPathAnexo = new JTextField();
	private JButton jbSelecionarBody = new JButton("...");
	private JButton jbSelecionarAnexo = new JButton("...");
	private JButton jbEnviar = new JButton("Enviar E-mail");
	
	public JFEmail() {
		
		jbSelecionarAnexo.addActionListener(new SelecionarArquivo(jtfPathAnexo));
		jbSelecionarBody.addActionListener(new SelecionarArquivo(jtfPathBody));
		jbEnviar.addActionListener(new ActionEnviar());
		
		setLayout(new MigLayout(new LC().noGrid()));
		add(new JLabel("Remetente"), new CC().wrap());
		add(jtfRemetente, new CC().width("500:100%:").wrap());
		add(new JLabel("Senha"), new CC().wrap());
		add(jpfPass, new CC().width("250:100%:").wrap());
		add(jcbSalvarSenha, new CC().wrap());
		add(new JLabel("SMTP"), new CC().wrap());
		add(jtfSmtp, new CC().width("250:100%:").wrap());
		add(new JLabel("Port"), new CC().wrap());
		add(jsPort, new CC().width("80"));
		add(jcbStartTLS, new CC().wrap());
		add(new JSeparator(), new CC().width("0:100%:").wrap());
		add(new JLabel("Título"), new CC().wrap());
		add(jtfTitulo, new CC().width("250:100%:").wrap());
		add(new JLabel("Destinatário"), new CC().wrap());
		add(jtfDestinatario, new CC().width("250:100%:").wrap());
		add(new JLabel("HTML Corpo"), new CC().wrap());
		add(jtfPathBody, new CC().width("250:100%:"));
		add(jbSelecionarBody, new CC().wrap());
		add(new JLabel("Anexo"), new CC().wrap());
		add(jtfPathAnexo, new CC().width("250:100%:"));
		add(jbSelecionarAnexo, new CC().wrap());
		add(jbEnviar, new CC());
		
		pack();
		setMinimumSize(getSize());
		setLocationRelativeTo(null);
		setTitle("Enviar e-mail");
		
		loadFromProperties();
	}
	
	private void loadFromProperties() {
		try {
			PropertiesUtils.loadProperties();
			
			jtfDestinatario.setText(PropertiesUtils.getProp(Prop.DESTINATARIO));
			jtfSmtp.setText(PropertiesUtils.getProp(Prop.SMTP));
			jtfPathAnexo.setText(PropertiesUtils.getProp(Prop.PATH_ANEXO));
			jtfPathBody.setText(PropertiesUtils.getProp(Prop.PATH_BODY));
			jtfTitulo.setText(PropertiesUtils.getProp(Prop.TITULO));
			jtfRemetente.setText(PropertiesUtils.getProp(Prop.REMETENTE));
			
			String senha = PropertiesUtils.getProp(Prop.SENHA);
			String port = PropertiesUtils.getProp(Prop.PORTA);
			String startTLS = PropertiesUtils.getProp(Prop.START_TLS);
			
			if(port!=null && !port.isEmpty()){
				jsPort.setValue(Integer.parseInt(port));
			}
			if(startTLS!=null && !startTLS.isEmpty()){
				jcbStartTLS.setSelected(Boolean.parseBoolean(startTLS));
			}
			if(senha!=null && !senha.isEmpty()){
				jcbSalvarSenha.setSelected(true);
				jpfPass.setText(new String(Base64.getDecoder().decode(senha)));
			}
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(JFEmail.this, "Erro ao carregar informações! \n"+e.getMessage(), "Erro!", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void saveProperties() {
		try {
			
			PropertiesUtils.setProp(Prop.DESTINATARIO, jtfDestinatario.getText());
			PropertiesUtils.setProp(Prop.SMTP, jtfSmtp.getText());
			PropertiesUtils.setProp(Prop.PATH_ANEXO, jtfPathAnexo.getText());
			PropertiesUtils.setProp(Prop.PATH_BODY, jtfPathBody.getText());
			PropertiesUtils.setProp(Prop.TITULO, jtfTitulo.getText());
			PropertiesUtils.setProp(Prop.REMETENTE, jtfRemetente.getText());
			PropertiesUtils.setProp(Prop.PORTA, String.valueOf(jsPort.getValue()));
			PropertiesUtils.setProp(Prop.START_TLS, String.valueOf(jcbStartTLS.isSelected()));
			if(jcbSalvarSenha.isSelected()){
				PropertiesUtils.setProp(Prop.SENHA, Base64.getEncoder().encodeToString(String.valueOf(jpfPass.getPassword()).getBytes()));
			}else{
				PropertiesUtils.setProp(Prop.SENHA, "");
			}
			
			PropertiesUtils.saveProperties();
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(JFEmail.this, "Erro ao carregar informações! \n"+e.getMessage(), "Erro!", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private class SelecionarArquivo implements ActionListener{
		private JTextField jtfFile;

		public SelecionarArquivo(JTextField jtfFile){
			this.jtfFile = jtfFile;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = new JFileChooser();
			chooser.setDialogTitle("Selecionar arquivo");
			
			File file = new File(jtfFile.getText());
			if(file.exists()){
				chooser.setCurrentDirectory(file);
			}
			chooser.showOpenDialog(JFEmail.this);
			
			File selectFile = chooser.getSelectedFile();
			if(selectFile!=null){
				jtfFile.setText(selectFile.getAbsolutePath());
			}
		}
	}
	
	private class ActionEnviar implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent evt) {
			try {
				String destinatario = jtfDestinatario.getText();
				String smtp = jtfSmtp.getText();
				String porta = jsPort.getValue().toString();
				String startTLS = String.valueOf(jcbStartTLS.isSelected());
				String pathAnexo = jtfPathAnexo.getText();
				String pathBody = jtfPathBody.getText();
				String titulo = jtfTitulo.getText();
				final String rementente = jtfRemetente.getText();
				final String senha = String.valueOf(jpfPass.getPassword());
				
				if(senha.trim().isEmpty()){
					return;
				}

				Properties props = new Properties();
				props.put("mail.smtp.auth", "true");
				props.put("mail.smtp.starttls.enable", startTLS);
				props.put("mail.smtp.host", smtp);
				props.put("mail.smtp.port", porta);

				Session session = Session.getInstance(props, new Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(rementente, senha);
					}
				});
				
	            MimeMultipart mimeMultiPart = new MimeMultipart("related");

	            if(!pathBody.isEmpty()){
	            	MimeBodyPart mimeBody = new MimeBodyPart();
	            	mimeBody.setDataHandler(new DataHandler(new FileDataSource(pathBody)));
	            	mimeBody.addHeader("Content-Type", "text/html; charset=UTF-8");
	            	mimeBody.addHeader("Content-Transfer-Encoding", "8bit");
	            	mimeMultiPart.addBodyPart(mimeBody);
	            }
	            
	            if(!pathAnexo.isEmpty()){
	            	File fileAtt = new File(pathAnexo);
	            	byte[] bytes = Files.readAllBytes(fileAtt.toPath());
	            	MimeBodyPart mimeAtt = new MimeBodyPart(new InternetHeaders(), bytes);
	            	mimeAtt.setFileName(fileAtt.getName());
	            	mimeMultiPart.addBodyPart(mimeAtt);
	            }
	            
	            MimeMessage message = new MimeMessage(session);
	            message.addHeader("charset", "UTF-8");
	            message.setSentDate(new Date());
	            message.setFrom(new InternetAddress(rementente));
	            message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(destinatario));
				message.setSubject(titulo);
				message.setContent(mimeMultiPart);

				Transport.send(message);

				JOptionPane.showMessageDialog(JFEmail.this, "Sucesso!");
				saveProperties();

			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(JFEmail.this, "ERRO! \n"+e.getMessage(), "MSG", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
}
