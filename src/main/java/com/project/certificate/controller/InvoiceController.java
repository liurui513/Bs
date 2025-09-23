package com.project.certificate.controller;


import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.security.auth.x500.X500Principal;

import com.project.certificate.custom.AjaxResult;
import com.project.certificate.custom.JpgridUtils;
import com.project.certificate.entity.*;
import com.project.certificate.security.UserUtils;
import com.project.certificate.service.*;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.X509Extensions;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.bouncycastle.asn1.x500.style.RFC4519Style.serialNumber;

@Controller
@RequestMapping(value="/invoice")
public class InvoiceController {
	@Autowired
	private LesseeService lesseeDAO;
	@Autowired
	private InvoiceService invoiceDAO;
	@Autowired
	private UserUtils userUtils;
	@Autowired
	private LesseeAdminService lesseeAdminDAO;
	@Autowired
	private UserService userDAO;
	@Autowired
	private CertificatesService certificatesDAO;
	@Autowired
	private JournalService journalDAO;


	@RequestMapping(value="/all")
	public String all(ModelMap map) {
		SysUser sysuser = userUtils.getUser();
		User user = userDAO.findById(sysuser.getId());
		List<Lessee> list = lesseeDAO.findByLesseeAdminId(user.getLessee().getLesseeAdmin().getId());
		map.put("lessee", list);
		return "user/invoice_all";
	}
	
	@RequestMapping(value="/findById")
	public String findById() {
		return "admin/invoiceAll";
	}
	
	@RequestMapping(value="/findByIdhui")
	public String findById2() {
		return "admin/invoiceAllhui";
	}
	
	@RequestMapping(value="/statistics")
	public String statistics() {
		return "system/statistics";
	}
	
	/* 
	 * 查询证书
	 */
	@RequestMapping(value="/list")
	@ResponseBody
	public Object list(JpgridUtils form, String number,String state) {
		SysUser user = userUtils.getUser();
		//System.out.println("111111111111"+user.getUsername());
		User user2 = userDAO.findById(user.getId());
		//System.out.println("22222222"+user2.getLessee().getLinkman());//小小
		Pageable pageable = form.buildPageable();
		Page<Invoice> page = null;
		//String name = user2.getLessee().getLinkman();
		Specification<Invoice> spec = new Specification<Invoice>() {
			@Override
			public Predicate toPredicate(Root<Invoice> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
				List<Predicate> rules = new ArrayList<>();
				/*if(StringUtils.hasText(name)) {
					rules.add(criteriaBuilder.like(root.get("name"), "%"+name+"%"));
				}else*/ if(StringUtils.hasText(number)) {
					rules.add(criteriaBuilder.like(root.get("number"), "%"+number+"%"));
				}else if(StringUtils.hasText(state)) {
					rules.add(criteriaBuilder.like(root.get("state"), "%" + state + "%"));
				}
				rules.add(criteriaBuilder.equal(root.get("lessee").get("id"), user2.getLessee().getId()));
				return criteriaBuilder.and(rules.toArray(new Predicate[rules.size()]));
			}			
		};
		page = invoiceDAO.findAll(spec, pageable);
		HashMap<String , Object> result = form.getPageResult(page);
		return result;
	}
	
	/* 
	 * 申请管理 
	 */
	@RequestMapping(value="/list1")
	@ResponseBody
	public Object list1(JpgridUtils form, String state, String code, String unit) {
		SysUser user = userUtils.getUser(); 
		LesseeAdmin lesseeAdmin = lesseeAdminDAO.findById(user.getId());//只有admin才能操作，
		Pageable pageable = form.buildPageable();
		Page<Invoice> page = null;
		System.out.println(unit+"1111111111111111");////////////////////////////这是空的????????????????????????????//
		//System.out.println(code+"222222222222222222222");
		Specification<Invoice> spec = new Specification<Invoice>() {
			@Override
			public Predicate toPredicate(Root<Invoice> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
				List<Predicate> rules = new ArrayList<>();
				if(StringUtils.hasText(state)) {
					rules.add(criteriaBuilder.like(root.get("state"), "%"+state+"%"));
				}else if(StringUtils.hasText(code)) {
					rules.add(criteriaBuilder.like(root.get("code"), "%"+code+"%"));
				}else if(StringUtils.hasText(unit)) {
					rules.add(criteriaBuilder.like(root.get("lessee").get("name"), "%"+unit+"%"));
				}
				rules.add(criteriaBuilder.lt(root.get("zstype"), 2));
				rules.add(criteriaBuilder.equal(root.get("lessee").get("lesseeAdmin").get("id"), user.getId()));
				return criteriaBuilder.and(rules.toArray(new Predicate[rules.size()]));
			}			
		};
		page = invoiceDAO.findAll(spec, pageable);
		HashMap<String , Object> result = form.getPageResult(page);
		return result;
	}

	/*
	下载自签名证书
	*/
	@RequestMapping(value="/bbb")
	public ResponseEntity<InputStreamResource> bbb() throws Exception {
		SysUser sysuser = userUtils.getUser();
		User user = userDAO.findById(sysuser.getId());
		//List<Lessee> list = lesseeDAO.findByLesseeAdminId(user.getLessee().getLesseeAdmin().getId());
		String name = sysuser.getName()+user.getLessee().getName()+user.getLessee().getLesseeAdmin().getOu();
		//System.out.println(sysuser.getName()+user.getLessee().getName()+user.getLessee().getLesseeAdmin().getUnit());
		// 1. 生成证书
		Security.addProvider(new BouncyCastleProvider());// 注册 Bouncy Castle 提供者
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		keyPairGenerator.initialize(2048);
		KeyPair keyPair = keyPairGenerator.generateKeyPair();
		X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();
		certGen.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
		certGen.setSubjectDN(new X500Principal("CN=name,C=CN"));
		certGen.setIssuerDN(new X500Principal("CN=name,C=CN"));
		certGen.setPublicKey(keyPair.getPublic());
		certGen.setNotBefore(new Date(System.currentTimeMillis() - 86400000L * 30));//30天前
		certGen.setNotAfter(new Date(System.currentTimeMillis() + 86400000L * 365 * 10));//10年后
		certGen.setSignatureAlgorithm("SHA256WithRSAEncryption");
		certGen.addExtension(X509Extensions.BasicConstraints, true, new BasicConstraints(false));
		certGen.addExtension(X509Extensions.KeyUsage, true, new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyEncipherment));
		X509Certificate cert = certGen.generate(keyPair.getPrivate(), "BC");
		// 2. 将证书转换为 PEM 格式的字节流
		ByteArrayOutputStream pemStream = new ByteArrayOutputStream();
		pemStream.write("-----BEGIN CERTIFICATE-----\n".getBytes());
		pemStream.write(Base64.getEncoder().encode(cert.getEncoded()));
		pemStream.write("\n-----END CERTIFICATE-----\n".getBytes());
		byte[] pemBytes = pemStream.toByteArray();
		// 3. 返回文件下载
		//System.out.println("abc");
		ByteArrayInputStream certInputStream = new ByteArrayInputStream(pemBytes);
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=example.crt");
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		//System.out.println("def");
		return ResponseEntity
				.ok()
				.headers(headers)
				.contentLength(pemBytes.length)
				.body(new InputStreamResource(certInputStream));
	}

	/*
	 * 吊销管理
	 */		
	@RequestMapping(value="/listhui")
	@ResponseBody
	public Object listhui(JpgridUtils form, String state, String code, String ou) {
		SysUser user = userUtils.getUser(); 
		LesseeAdmin lesseeAdmin = lesseeAdminDAO.findById(user.getId());
		Pageable pageable = form.buildPageable();
		Page<Invoice> page = null;
		System.out.println(ou+"222222222222222");
		Specification<Invoice> spec = new Specification<Invoice>() {
			@Override
			public Predicate toPredicate(Root<Invoice> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
				List<Predicate> rules = new ArrayList<>();
				if(StringUtils.hasText(state)) {
					rules.add(criteriaBuilder.like(root.get("state"), "%"+state+"%"));
				}else if(StringUtils.hasText(code)) {
					rules.add(criteriaBuilder.like(root.get("code"), "%"+code+"%"));
				}else if(StringUtils.hasText(ou)) {
					rules.add(criteriaBuilder.like(root.get("lessee").get("name"), "%"+ou+"%"));
				}
				rules.add(criteriaBuilder.gt(root.get("zstype"), 1));
				rules.add(criteriaBuilder.equal(root.get("lessee").get("lesseeAdmin").get("id"), user.getId()));
				return criteriaBuilder.and(rules.toArray(new Predicate[rules.size()]));
				}			
			};
		page = invoiceDAO.findAll(spec, pageable);
		HashMap<String , Object> result = form.getPageResult(page);
		return result;
		}
		
	
	/* 
	 * 验证号码是否已添加 
	 */
	@RequestMapping(value="/number")
	@ResponseBody
	public Boolean number(String number) {
		Invoice invoice = invoiceDAO.findByNumber(number);
		if(invoice == null) {
			return false;
		}else {
			return true;
		}
	}
	public Boolean username(String username) {
		Lessee lessee = lesseeDAO.findByLinkman(username);
		if(lessee == null) {
			return false;
		}else {
			return true;
		}
	}
	//申请证书
	@RequestMapping(value="/save")
	@ResponseBody
	public Object save(Invoice model,String publickey) {
		//System.out.println(publickey);
		SysUser sysuser = userUtils.getUser();
		Lessee lessee = lesseeDAO.findByLinkman(sysuser.getName());//有的员工不是公司的联系人
		User user2 = userDAO.findById(sysuser.getId());
		Date d = new Date(); 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		String dateNowStr = sdf.format(d);
		System.out.println("用户名："+sysuser.getUsername());
		System.out.println("公司号："+lessee.getName());
		System.out.println("不知道是啥"+user2.getLessee().getName());
		if(model.getCode() == null || model.getCode() == "") {
			Calendar now = Calendar.getInstance();//获取当前时间
			String year = String.valueOf(now.get(Calendar.YEAR));  
			String month = String.format("%02d", now.get(Calendar.MONTH) + 1); // 确保月份是两位数
			String day = String.format("%02d", now.get(Calendar.DAY_OF_MONTH)); // 确保天数是两位数
			String intFlag = String.valueOf((int)(Math.random() * 10000 + 1));
			model.setDate(dateNowStr);
	        model.setSerial(year+month+day+intFlag);//编号
	        model.setCode(intFlag+month+day+intFlag);//代码
	        model.setState("证书申请中");
	        model.setLessee(user2.getLessee());
	        model.setZstype(0);
	        model.setPublicKey(publickey);
	        invoiceDAO.save(model);
	        SysUser user = userUtils.getUser();
			System.out.println("添加证书");
		}else {
			System.out.println("吊销申请123456");
			model.setDate(dateNowStr);
			model.setState("吊销申请中");
			model.setZstype(2);
			model.setLessee(user2.getLessee());
			invoiceDAO.save(model);
			System.out.println("吊销完成");
		}
		Integer a = lessee.getNumber()+1;
		lessee.setNumber(a);
		lesseeDAO.save(lessee);
		SysUser user = userUtils.getUser();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		String data = df.format(new Date());
		Journal journal = new Journal();
		journal.setDate(data);
		journal.setUsername(user.getUsername());
		journal.setOperationName("证书申请");
		System.out.println(journal.getOperationName());
		journalDAO.save(journal);
		return AjaxResult.build(true, "OK");
	}

	public KeyStore abc() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
		KeyStore store = KeyStore.getInstance("PKCS12");
		store.load(null,null);
		return store;
	}
	// 创建KeyStore,存储证书
	//加载(初始化)一个空的密钥库
	//第一个null表示不提供InputStream(因为我们要创建新的密钥库)
	//第二个null表示不使用密码保护整个密钥库
	@Value("${security.publickey}")
	private String publicKeyStr;

	@Value("${security.privatekey}")
	private String privateKeyStr;
	/*
	 * 申请审核通过
	 */
	@RequestMapping(value="/auditok")
	@ResponseBody
	public void auditok(String ids,Certificates model) throws IOException, NoSuchAlgorithmException, CertificateException, org.bouncycastle.operator.OperatorCreationException, ParseException, KeyStoreException, InvalidKeySpecException {
		//证书颁发者信息
		//System.out.println("123456");
		//System.out.println(ids);
		SysUser sysuser = userUtils.getUser();
		//System.out.println(sysuser.getName()+sysuser.getUsername()+sysuser.getId());
		LesseeAdmin lesseeAdmin =lesseeAdminDAO.findById(sysuser.getId());
		//System.out.println(lesseeAdmin.getId()+lesseeAdmin.getOu()+lesseeAdmin.getO());
		//System.out.println(lesseeAdmin.toString());
		String[] id = ids.split(",");
		//System.out.println("67890");
		for(int i = 0; i < id.length; i++) {
			Invoice invoice = invoiceDAO.findById(Integer.parseInt(id[i]));
			invoice.setState("证书使用中");
			invoice.setZstype(1);
			Date d = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String dateNowStr = sdf.format(d);
			long oneYearInMillis = 365L * 24 * 60 * 60 * 1000;
			Date oneYearLater = new Date(d.getTime() + oneYearInMillis);
			// 格式化一年后的日期
			String enddate = sdf.format(oneYearLater);
			invoice.setDate(dateNowStr);
			invoice.setEndDate(enddate);
			invoiceDAO.save(invoice);

			// 生成证书所需密钥对，RSA 算法，密钥长度 2048 字节
		/*	KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(2048);
			KeyPair keyPair = keyPairGenerator.generateKeyPair();
			PrivateKey privateKey = keyPair.getPrivate();
			//PublicKey publicKey = keyPair.getPublic();*/

			byte[] keyByte = Base64.getDecoder().decode(privateKeyStr);
			PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyByte);
			KeyFactory kf = KeyFactory.getInstance("RSA");
			PrivateKey caprivateKey = kf.generatePrivate(spec);//CA私钥

			byte[] keyBytes = Base64.getDecoder().decode(publicKeyStr);
			X509EncodedKeySpec spec1 = new X509EncodedKeySpec(keyBytes);
			KeyFactory kf1 = KeyFactory.getInstance("RSA");
			PublicKey capublicKey =  kf1.generatePublic(spec1);//CA公钥

			//证书颁发者信息
			String issuer = lesseeAdmin.toString();
			X500Name issueDn = new X500Name(issuer);
			//证书主题信息
			Lessee lessee = lesseeDAO.findById(invoice.getLessee().getId());
			System.out.println(lessee.toString());
			String subject =lessee.toString();
			X500Name subjectDn = new X500Name(subject);
			//组装用户公钥信息


/*			byte[] key = Base64.getDecoder().decode(invoice.getPublicKey());
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(key);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PublicKey userpublickey =  keyFactory.generatePublic(keySpec);


			SubjectPublicKeyInfo subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(userpublickey);*/

			byte[] key = Base64.getDecoder().decode(invoice.getPublicKey());
			SubjectPublicKeyInfo subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(key); // 直接使用原始DER编码


			BigInteger serial=new BigInteger(invoice.getSerial());
			Date notBefore = sdf.parse(invoice.getDate());
			Date notAfter = sdf.parse(invoice.getEndDate());
			X509v3CertificateBuilder builder = new X509v3CertificateBuilder(issueDn,serial,notBefore,notAfter,subjectDn,subjectPublicKeyInfo);
			//证书的签名
			Security.addProvider(new BouncyCastleProvider());// 注册 Bouncy Castle 提供者
			System.out.println("开始证书签名");
			ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA")
					.setProvider("BC") // 使用Bouncy Castle作为提供者
					.build(caprivateKey);//应该使用CA私钥
			System.out.println("证书签名成功");
			X509CertificateHolder holder = builder.build(signer);
			//System.out.println("a");
			byte[] certBuf = holder.getEncoded();
			//System.out.println("b");
			X509Certificate certificate = (X509Certificate) CertificateFactory.getInstance("X509").generateCertificate(new ByteArrayInputStream(certBuf));
			//System.out.println("c");
			//System.out.println(certificate);
			String keyPassword = invoice.getSerial();
			KeyStore store = abc();
			store.setKeyEntry(sysuser.getName(),caprivateKey,keyPassword.toCharArray(),new Certificate[]{ certificate});
			FileOutputStream fileOutputStream = new FileOutputStream("D:\\1\\certificate_admin.cer");
			store.store(fileOutputStream, keyPassword.toCharArray());
			fileOutputStream.close();
			System.out.println("文件生成");
			model.setSerialNumber(invoice.getSerial());
			model.setSubjectDn(subject);
			model.setIssuerDn(issuer);
			model.setNotBefore(notBefore);
			model.setNotAfter(notAfter);
			model.setStatus(false);
			//String publickey = Base64.getEncoder().encodeToString(invoice.getPublicKey());
			model.setPublicKey(invoice.getPublicKey());
			model.setInvoiceid(Integer.parseInt(id[i]));
			certificatesDAO.save(model);
		}
	}
	/*
        下载证书
        */
	@RequestMapping(value="/aaa")
	@ResponseBody
	public ResponseEntity<InputStreamResource> aaa(String id) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException {
		System.out.println("34567");
		System.out.println(id);
		SysUser sysuser = userUtils.getUser();
		System.out.println(sysuser.getName()+sysuser.getUsername()+sysuser.getId());
		LesseeAdmin lesseeAdmin =lesseeAdminDAO.findById(sysuser.getId());
		Invoice invoice = invoiceDAO.findById(Integer.parseInt(id));
		System.out.println(invoice.getSerial());
		char[] charArray = invoice.getSerial().toCharArray();
		KeyStore ks = abc();
		ks = KeyStore.getInstance("PKCS12");
		try (FileInputStream fis = new FileInputStream("D:\\1\\certificate_admin.cer")) {
			ks.load(fis, charArray);
		}
		System.out.println("keystore type=" + ks.getType());
		Enumeration<String> enumas = ks.aliases();
		String keyAlias = null;
		if (enumas.hasMoreElements()) {
			keyAlias = enumas.nextElement();
			System.out.println("alias=[" + keyAlias + "]");
		}
		System.out.println("is key entry=" + ks.isKeyEntry(keyAlias));
		PrivateKey prikey = (PrivateKey) ks.getKey(keyAlias, charArray);
		Certificate cert = ks.getCertificate(keyAlias);
		PublicKey pubkey = cert.getPublicKey();
		System.out.println("cert class = " + cert.getClass().getName());
		System.out.println("cert = " + cert);
		System.out.println("public key = " + pubkey);
		System.out.println("private key = " + prikey);
		//下载证书
		// 2. 将证书转换为 PEM 格式的字节流
		// 证书部分
		ByteArrayOutputStream certStream = new ByteArrayOutputStream();
		certStream.write("-----BEGIN CERTIFICATE-----\n".getBytes());
		certStream.write(Base64.getEncoder().encode(cert.getEncoded()));
		certStream.write("\n-----END CERTIFICATE-----\n".getBytes());
		System.out.println("z证书");
		// 公钥部分（PEM格式）
		ByteArrayOutputStream pubKeyStream = new ByteArrayOutputStream();
		pubKeyStream.write("-----BEGIN PUBLIC KEY-----\n".getBytes());
		pubKeyStream.write(Base64.getEncoder().encode(pubkey.getEncoded()));
		pubKeyStream.write("\n-----END PUBLIC KEY-----\n".getBytes());
		System.out.println("公钥");
		// 打包为ZIP（需要额外依赖）
		ByteArrayOutputStream zipStream = new ByteArrayOutputStream();
		try (ZipOutputStream zos = new ZipOutputStream(zipStream)) {
			// 添加证书
			zos.putNextEntry(new ZipEntry("certificate001.cer"));
			zos.write(certStream.toByteArray());
			zos.closeEntry();
			System.out.println("压缩包");
			// 添加公钥
			zos.putNextEntry(new ZipEntry("public_key001.cer"));
			zos.write(pubKeyStream.toByteArray());
			zos.closeEntry();
		}
		byte[] zipBytes = zipStream.toByteArray();

// 返回ZIP下载
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=certificate_bundle.zip");
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

		return ResponseEntity.ok()
				.headers(headers)
				.contentLength(zipBytes.length)
				.body(new InputStreamResource(new ByteArrayInputStream(zipBytes)));
	}

	/* 
	 * 吊销审核通过 
	 */
	@RequestMapping(value="/audithuiok")
	@ResponseBody
	public void audithuiok(String ids) {
		String[] id = ids.split(",");
		for (int i = 0; i < id.length; i++) {
			Invoice invoice = invoiceDAO.findById(Integer.parseInt(id[i]));
			System.out.println(invoice.getRevocationReason()+"12344567");
			invoice.setState("证书已吊销");
			invoice.setZstype(3);
			Date d = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String dateNow = sdf.format(d);
			invoice.setEndDate(dateNow);
			invoiceDAO.save(invoice);
			//对证书审核要生成证书的详细步骤，要用到详细的密钥算法啥的
			certificatesDAO.revokeCertificate(invoice.getSerial(), invoice.getRevocationReason());  // 调用服务方法

		}
	}


	/*
	 * 删除
	 */
	/*@RequestMapping(value="/delete")
	@ResponseBody
	public void delete(String ids) {
		String[] id = ids.split(",");
		for (int i = 0; i < id.length; i++) {
			Invoice invoice = invoiceDAO.findById(Integer.parseInt(id[i]));
			System.out.println(invoice.getZstype());
			if (invoice.getZstype() == 0 || invoice.getZstype() == 2) {
				invoiceDAO.deleteById(Integer.parseInt(id[i]));
				if (certificatesDAO.findByInvoiceid(Integer.parseInt(id[i])) != null) {
					certificatesDAO.deleteByInvoiceid(Integer.parseInt(id[i]));
					System.out.println("删除成功");
				}
			} else {
				System.out.println("证书不能删除");
			}
		}
	}
*/
	@RequestMapping(value="/delete")
	@ResponseBody
	public Map<String, Object> delete(String ids) {
		Map<String, Object> result = new HashMap<>();
		List<Integer> cannotDeleteIds = new ArrayList<>(); // 记录不能删除的ID
		List<Integer> deletedIds = new ArrayList<>();      // 记录已删除的ID

		String[] idArray = ids.split(",");
		for (String idStr : idArray) {
			int id = Integer.parseInt(idStr);
			Invoice invoice = invoiceDAO.findById(id);

			if (invoice == null) {
				cannotDeleteIds.add(id);
				continue;
			}

			if (invoice.getZstype() == 0 || invoice.getZstype() == 2) {
				invoiceDAO.deleteById(id);
				if (certificatesDAO.findByInvoiceid(id) != null) {
					certificatesDAO.deleteByInvoiceid(id);
				}
				deletedIds.add(id);
			} else {
				cannotDeleteIds.add(id);
			}
		}

		result.put("success", true);
		result.put("deletedIds", deletedIds);
		result.put("cannotDeleteIds", cannotDeleteIds);

		return result;
	}

		//}

/*		private class OperatorCreationException extends Exception {
		}*/
	}