package com.bplead.cad.util;

import java.awt.Desktop;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Logger;

import com.bplead.cad.bean.DataContent;
import com.bplead.cad.bean.SimpleDocument;
import com.bplead.cad.bean.SimpleFolder;
import com.bplead.cad.bean.SimplePdmLinkProduct;
import com.bplead.cad.bean.client.Temporary;
import com.bplead.cad.bean.constant.RemoteMethod;
import com.bplead.cad.bean.io.Attachment;
import com.bplead.cad.bean.io.AttachmentModel;
import com.bplead.cad.bean.io.Document;
import com.bplead.cad.model.CustomPrompt;

import priv.lee.cad.util.ClientAssert;
import priv.lee.cad.util.ClientInstanceUtils;
import priv.lee.cad.util.PropertiesUtils;
import priv.lee.cad.util.StringUtils;

public class ClientUtils extends ClientInstanceUtils {

	public static StartArguments args = new StartArguments();
	private static final int BUFFER_SIZE = 2 * 1024;
	private final static String CAD_PRIMARY_SUFFIX = "wt.document.primary.file.suffix";
	public static String cadPrimarySuffix = PropertiesUtils.readProperty(CAD_PRIMARY_SUFFIX);
	private final static String CAPP_PRIMARY_SUFFIX = "capp.document.primary.file.suffix";
	public static String cappPrimarySuffix = PropertiesUtils.readProperty(CAPP_PRIMARY_SUFFIX);
	private static final String CONFIG_SUFFIX = "wt.document.config.file.suffix";
	public static String configSuffix = PropertiesUtils.readProperty(CONFIG_SUFFIX);
	private static final Logger logger = Logger.getLogger(ClientUtils.class);
	private static final String OID = "oid";
	public static Temporary temprary = new Temporary();
	private static final String ZIP = ".zip";

	public static List<Attachment> buildAttachments(AttachmentModel model, String primarySuffix) {
		List<Attachment> attachments = model.getAttachments();
		for (Attachment attachment : attachments) {
			File file = new File(attachment.getAbsolutePath());
			attachment.setName(file.getName());
			attachment.setPrimary(file.getName().endsWith(primarySuffix));
		}
		return attachments;
	}

	public static boolean checkin(Document document) {
		ClientAssert.notNull(document, "Document is required");
		return invoke(RemoteMethod.CHECKIN, new Class<?>[] { Document.class }, new Object[] { document },
				Boolean.class);
	}

	public static DataContent checkoutAndDownload(List<SimpleDocument> documents) {
		ClientAssert.notEmpty(documents, "Documents is requried");
		return invoke(RemoteMethod.CHECKOUT_DOWNLOAD, new Class<?>[] { List.class }, new Object[] { documents },
				DataContent.class);
	}

	private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
		byte[] bytesIn = new byte[BUFFER_SIZE];
		int read = 0;
		while ((read = zipIn.read(bytesIn)) != -1) {
			bos.write(bytesIn, 0, read);
		}
		bos.close();
	}

	public static String getDocumentOid(String primarySuffix, List<Attachment> attachments) {
		try {
			File configFile = null;
			for (Attachment attachment : attachments) {
				if (attachment.isPrimary()) {
					configFile = new File(attachment.getAbsolutePath().replace(primarySuffix, configSuffix));
					break;
				}
			}

			if (configFile != null && configFile.exists()) {
				Properties properties = new Properties();
				properties.load(new FileInputStream(configFile));
				return properties.getProperty(OID);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static SimpleFolder getSimpleFolders(SimplePdmLinkProduct product) {
		ClientAssert.notNull(product, "Product is requried");
		return invoke(RemoteMethod.GET_SIMPLE_FOLDERS, new Class<?>[] { SimplePdmLinkProduct.class },
				new Object[] { product }, SimpleFolder.class);
	}

	@SuppressWarnings("unchecked")
	public static List<SimplePdmLinkProduct> getSimplePdmLinkProducts() {
		return invoke(RemoteMethod.GET_SIMPLE_PRODUCTS, null, null, List.class);
	}

	public static void open(File directory) {
		if (directory == null) {
			return;
		}

		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				open(file);
			} else {
				if (file.getName().endsWith(cappPrimarySuffix) || file.getName().endsWith(cadPrimarySuffix)) {
					try {
						Desktop.getDesktop().open(file);
					} catch (IOException e) {
						logger.error("Failed to open file:" + file.getAbsolutePath());
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static List<SimpleDocument> search(String number, String name) {
		ClientAssert.isTrue(StringUtils.hasText(number) || StringUtils.hasText(name), "Number or name is requried");
		return invoke(RemoteMethod.SEARCH, new Class<?>[] { String.class, String.class }, new Object[] { number, name },
				List.class);
	}

	public static File unzip(File zipFile) {
		ClientAssert.notNull(zipFile, "Zip file is required");

		File directory = zipFile.getParentFile();
		try {
			ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFile));
			ZipEntry entry = zipIn.getNextEntry();
			while (entry != null) {
				String filePath = directory.getAbsolutePath() + File.separator + entry.getName();
				if (!entry.isDirectory()) {
					File file = new File(filePath);
					if (!file.getParentFile().exists()) {
						file.getParentFile().mkdirs();
					}
					extractFile(zipIn, filePath);
				}
				zipIn.closeEntry();
				entry = zipIn.getNextEntry();
			}
			zipIn.close();
		} catch (IOException e) {
			e.printStackTrace();
			ClientAssert.isTrue(true, CustomPrompt.FAILD_OPTION);
		} finally {
			zipFile.delete();
		}
		return new File(directory, zipFile.getName().replace(ZIP, ""));
	}

	public static class StartArguments {

		public static final String CAD = "cad";
		public static final String CAPP = "capp";
		public static final String DOWNLOAD = "download";
		private String type;

		public StartArguments() {

		}

		public StartArguments(String type) {
			this.type = type;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}
	}
}
