package com.powerhigh.gdfas.rmi;

import java.util.ArrayList;
import java.util.HashMap;


public interface parse
{
	public void test() throws Exception ;	
	
	public  String sendAFN0AF10(String txfs,String xzqxm, String zddz,
		     int ksxh,int jsxh) throws Exception ;	
	
	public  String sendAFN01F1(String txfs,String xzqxm, String zddz,
		     String csz) throws Exception ;	
	
	
	public  String sendAFN01F2(String txfs,String xzqxm, String zddz,
		     String csz) throws Exception ;	
	
	
	public  String sendAFN01F3(String txfs,String xzqxm, String zddz,
		     String csz) throws Exception ;	
	
	
	public  String sendAFN01F4(String txfs,String xzqxm, String zddz,
		     String csz) throws Exception ;	
	
	
	public  String sendAFN04F1(String txfs,String xzqxm, String zddz,
			     String csz) throws Exception ;	
	
	public  String sendAFN04F3(String txfs,String xzqxm, String zddz, 
			  String csz) throws Exception;	

	public  String sendAFN04F4(String txfs,String xzqxm, String zddz, 
			  String csz) throws Exception;
	
	public  String sendAFN04F5(String txfs,String xzqxm, String zddz, 
			  String csz) throws Exception;
	
	public  String sendAFN04F6(String txfs,String xzqxm, String zddz, 
			  String csz) throws Exception;

	public  String sendAFN04F7(String txfs,String xzqxm, String zddz, 
			  String csz) throws Exception;
	
	public  String sendAFN04F8(String txfs,String xzqxm, String zddz, 
			  String csz) throws Exception ;
	
	public  String sendAFN04F9(String txfs,String xzqxm, String zddz, 
			  String csz) throws Exception;
	
	public  String sendAFN04F10(String txfs,String xzqxm, String zddz, 
			  String csz) throws Exception;
	
	public  String sendAFN04F11(String txfs,String xzqxm, String zddz, 
			  String csz) throws Exception;
	
	public  String sendAFN04F12(String txfs,String xzqxm, String zddz, 
			  String csz) throws Exception;
	
	public  String sendAFN04F13(String txfs,String xzqxm, String zddz,
			  String csz) throws Exception;

	public  String sendAFN04F14(String txfs,String xzqxm, String zddz,String cldh,
			  String csz) throws Exception;
	
	public String sendAFN04F15(String txfs, String xzqxm, String zddz,String cldh,
			String csz) throws Exception;
	
	public String sendAFN04F16(String txfs, String xzqxm, String zddz,String cldh,
			String csz) throws Exception;
	public String sendAFN04F17(String txfs, String xzqxm, String zddz,
			String csz) throws Exception;
	public String sendAFN04F18(String txfs, String xzqxm, String zddz,
			String csz) throws Exception;
	
//	public  String sendAFN04F18(String txfs,String xzqxm, String zddz,
//			  String csz) throws Exception;
	
	public  String sendAFN04F25(String txfs,String xzqxm, String zddz, 
			  int cldh,String csz) throws Exception;
	
	public  String sendAFN04F26(String txfs,String xzqxm, String zddz,String csz) throws Exception ;
	public  String sendAFN04F27(String txfs,String xzqxm, String zddz,String csz) throws Exception ;

	public  String sendAFN04F28(String txfs,String xzqxm, String zddz, 
			String cldh,String csz) throws Exception ;
	
	public  String sendAFN04F33(String txfs,String xzqxm, String zddz, 
			String csz) throws Exception ;
	
	public  String sendAFN04F34(String txfs,String xzqxm, String zddz, 
			String csz) throws Exception ;
	
	public  String sendAFN04F36(String txfs,String xzqxm, String zddz, 
			String csz) throws Exception ;
	
	public  String sendAFN04F57(String txfs,String xzqxm, String zddz, 
			  String csz) throws Exception ;
	
	public  String sendAFN04F59(String txfs,String xzqxm, String zddz,
				String csz) throws Exception ;
	
	public  String sendAFN04F60(String txfs,String xzqxm, String zddz, 
			  String csz) throws Exception;
	
	public  String sendAFN04F61(String txfs,String xzqxm, String zddz,
			  String csz) throws Exception ;

	public  String sendAFN04F65(String txfs,String xzqxm, String zddz, 
				String rwh,String csz) throws Exception;

	public  String sendAFN04F66(String txfs,String xzqxm, String zddz, 
				String rwh,String csz) throws Exception;
	
	 public  String sendAFN04F67(String txfs,String xzqxm, String zddz, 
				String rwh,String csz) throws Exception;
	 
	 public  String sendAFN04F68(String txfs,String xzqxm, String zddz, 
				String rwh,String csz) throws Exception;
	
	public  String sendAFN04F81(String txfs,String xzqxm, String zddz,
			  String zlmnldkh,String csz) throws Exception;	

	public  String sendAFN04F82(String txfs,String xzqxm, String zddz,
			  String zlmnldkh,String csz) throws Exception;
	
	public  String sendAFN04F83(String txfs,String xzqxm, String zddz,
			  String zlmnldkh,String csz) throws Exception;

	public  String sendAFN05F29(String txfs,String xzqxm, String zddz) throws Exception ;
	
	public  String sendAFN05F1(String txfs,String xzqxm, String zddz,String cldh,String csz) throws Exception ;
	
	public  String sendAFN05F2(String txfs, String xzqxm, String zddz,String cldh, String csz) throws Exception ;
	
	public  String sendAFN05F3(String txfs, String xzqxm, String zddz,String cldh) throws Exception ;

	public  String sendAFN05F31(String txfs,String xzqxm, String zddz,String csz) throws Exception ;
	
    public   String sendZdds(String txfs,String xzqxm, String zddz,String rq) 
		throws Exception;
    public   String sendZdfw(String txfs,String xzqxm, String zddz,String fwlx) 
		throws Exception;
    public   String queryRwsj(String txfs,String xzqxm, String zddz, String rwlx,int rwh,String qssj) 
		throws Exception;
    
    public   String queryZdsj(String txfs,String xzqxm, String zddz,String sjlx,int sjqszz,int sjjszz) 
		throws Exception;
    
    public   String sendCldjbcspz(String txfs,String xzqxm, String zddz, int cldh,HashMap cldjbcs) 
		throws Exception;
        
    public   String sendYk(String txfs,String xzqxm, String zddz, String lch, String ykbz,String xdsj,String gjyssj)
		throws Exception;
    
   
    public   String sendDnlfl(String txfs,String xzqxm, String zddz, String[][] fl)
		throws Exception;
    
    public   String sendCfgjcs(String txfs,String xzqxm, String zddz, String cfgjcs) 
		throws Exception;
    
    public   String sendCfgjtrbz(String txfs,String xzqxm, String zddz, String trbz)
		throws Exception;
    
    public   String sendDnlflsd(String txfs,String xzqxm, String zddz, String[][] sd)
		throws Exception;
    
    public   String sendCxgkcs(String txfs,String xzqxm, String zddz, int zjzh,String cxkdz,String dzzf,String dzxs,String xdqssj,String xdyxsj,String mzxdr) 
		throws Exception;
    
    public   String sendCxgktrbz(String txfs,String xzqxm, String zddz, int zjzh,String trbz) 
		throws Exception;
    
    public   String sendAFN04F43(String txfs,String xzqxm, String zddz, int zjzh,String hcsj) 
		throws Exception;
    
    public   String sendDklc(String txfs,String xzqxm, String zddz, int zjzh,String[] lc) 
		throws Exception;
    
    public   String sendYdkdz(String txfs,String xzqxm, String zddz, int zjzh, String dz,String dzfh,String dzdw)
		throws Exception;
    
    public   String sendGdkdz(String txfs,String xzqxm, String zddz, int zjzh,String gddh,String bz,String gdfh,String gdz,String bjmxfh,String bjmxz,String tzmxfh,String tzmxz)
		throws Exception;
    
    public   String sendYdktrbz(String txfs,String xzqxm, String zddz, int zjzh,String trbz)
		throws Exception;
    
    public   String sendGdktrbz(String txfs,String xzqxm, String zddz, int zjzh,String trbz)
		throws Exception;
    
    public   String sendGklc(String txfs,String xzqxm, String zddz, int zjzh,String[] lc) 
		throws Exception;
    
    public   String sendAFN04F41(String txfs,String xzqxm, String zddz, int zjzh,HashMap sd) 
		throws Exception;
    
    public   String sendAFN05F9F17(String txfs,String xzqxm, String zddz, int zjzh,String trbz,String fabh,String[] trsd) 
		throws Exception;
    
    public   String sendSfyxzdyzzth(String txfs,String xzqxm, String zddz, String sfyx) 
		throws Exception;
    
    public   String sendSfyxzdzdsb(String txfs,String xzqxm, String zddz, String sfyxzdsb) 
		throws Exception;
    
    public   String sendSfzdtctr(String txfs,String xzqxm, String zddz, String sftctr) 
		throws Exception;
    
    public   String sendYybtkcs(String txfs,String xzqxm, String zddz, int zjzh,String btqssj,String btjssj,String btkgldz,String dzzf,String dzxs) 
		throws Exception;
    
    public   String sendYybtktrbz(String txfs,String xzqxm, String zddz, int zjzh,String trbz) 
		throws Exception;
    
    public   String sendZdbadz(String txfs,String xzqxm, String zddz, String badz,String xs,String zf) 
		throws Exception;
    
    public   String sendZdbd(String txfs,String xzqxm, String zddz, String bdsj) 
		throws Exception;
    
    public   String sendZdcbjg(String txfs,String xzqxm, String zddz, int cbjg) 
		throws Exception;
    
    public   String sendZdcbr(String txfs,String xzqxm, String zddz, String day,String time) 
		throws Exception;
    
    public   String sendZddnbpz(String txfs,String xzqxm, String zddz, ArrayList dnbxx) 
		throws Exception;
    
//    public   String sendAFN04F18(String txfs,String xzqxm, String zddz, String[][] sd) 
//		throws Exception;
    
    public   String sendZdmcpz(String txfs,String xzqxm, String zddz, ArrayList mcxx) 
		throws Exception;
    
    public   String sendZdpzslb(String txfs,String xzqxm, String zddz,int dnbsl, int mcsl, int mnlsl, int zjzsl) 
		throws Exception;
    
    public   String sendZdrw(String txfs,String xzqxm, String zddz, String rwh,String fszq,String zqdw,String fsjzsj,String cqbl,String[][] rwsjx,String rwlx) 
		throws Exception;
    
    public   String sendZdrwqybz(String txfs,String xzqxm, String zddz,String rwlx,String rwh,String rwqybz) 
		throws Exception;
    
    public   String sendZdsjjl(String txfs,String xzqxm, String zddz, String sjjlyxbz,String sjzyxdjbz) 
		throws Exception;
    
    public   String sendZdzjzpz(String txfs,String xzqxm, String zddz, ArrayList zjzxx) 
		throws Exception;
    
    public   String sendXztz(String txfs,String xzqxm, String zddz,String wjm,byte[] wjnr,String ip,
				String port,String cxmklx,String cxjhsj)
    	throws Exception;
    
    public   String sendXzqx(String txfs,String xzqxm, String zddz)
    	throws Exception;
    
    public   String sendXzggcxjhsj(String txfs,String xzqxm, String zddz,String cxjhsj)
    	throws Exception;
    
    public    String sendXzcxbbqh(String txfs,String xzqxm, String zddz)
    	throws Exception;
    
    public   String query_1lsj(String txfs,String xzqxm, String zddz,String[][] sjxxx) 
    	throws Exception;
    public   String query_allzd_0cf2() 
        	throws Exception;
    
    public   String query_2lsj_qx(String txfs,String xzqxm, String zddz,
    		String[][] sjxxx,String qssj,String sjmd,String sjds)
    	throws Exception;
    
    public   String query_2lsj_rdj(String txfs,String xzqxm, String zddz,
    		String[][] sjxxx,String rdjsj)
    	throws Exception;
    
    public   String query_2lsj_ydj(String txfs,String xzqxm, String zddz,
    		String[][] sjxxx,String ydjsj)
		throws Exception;
    
    public   String query_zdcspz(String txfs,String xzqxm, String zddz,String[][] sjxxx)
		throws Exception;
    
    public   String query_zj(String txfs, String xzqxm, String zddz,
			String dbgylx, String dnbdz, String dnbsjxdm, String btl, 
			String tzw, String jym,
			String ws,String bwcssj,String zjcssj) throws Exception;
    
    public   String sendZdzdbd(String txfs,String xzqxm, String zddz, int zdbdsj)
		throws Exception;
    
    public   String sendZjzsjdjcs(String txfs,String xzqxm, String zddz,String zjzh, String[] djcs) 
		throws Exception;
    
    public   String sendCldsjdjcs(String txfs,String xzqxm, String zddz,String cldh, String[][] djcs) 
    	throws Exception;
    
    public   String sendZdtxcs(String txfs,String xzqxm, String zddz, String scjyssj,String fscsyxyssj,String ddcdzxycssj,String cfcs,String zdsbzysjjlqrbz,String zdsbybsjjlqrbz,String xtzq)
		throws Exception;
    
    public   String sendZzip(String txfs,String xzqxm, String zddz, String[] zyip,String[] byip,String[] wgip,String[] dlip,String apn)
		throws Exception;
    
    public   String sendZzdhhm(String txfs,String xzqxm, String zddz, String zzdhhm,String dxzxhm)
		throws Exception;
    
    public   String sendZtlsrcs(String txfs,String xzqxm, String zddz,String ztljrbz,String ztlsxbz,String ztlgjbz)
		throws Exception;
    
    public   String sendDnbycpbfz(String txfs,String xzqxm, String zddz,String dnlccfz,String dnbfzfz,String dnbtzfz,String dnbjsfz)
		throws Exception;
    
    public   String sendZlmnljrcs(String txfs,String xzqxm, String zddz,String jrbz)
		throws Exception;
    
    public   String sendDrqcs(String txfs,String xzqxm, String zddz,String cldh,String[][] drqcs)
		throws Exception;
    
    public   String sendDrqtqyxcs(String txfs,String xzqxm, String zddz,String cldh,
      		String mbglys,String mbglysfh,String trwgglmx,String qcwgglmx,
    		String yssj,String dzsjjg)
		throws Exception;
    
    public   String sendDrqbhcs(String txfs,String xzqxm, String zddz,String cldh,
      		String gdy,String gdyhcz,String qdy,String qdyhcz,
      		String dlsx,String dlsxfh,String dlyxhc,String dlyxhcfh,
      		String dysx,String dysxfh,String dyyxhc,String dyyxhcfh)
		throws Exception;
    
    public   String sendDrqtqkzfs(String txfs,String xzqxm, String zddz,String cldh,String kzfs)
		throws Exception;
    
    public   String sendGkgjsj(String txfs,String xzqxm,String zddz,String lch,String gkgjsj)
		throws Exception;
    
    public   String sendZddydlmnlpz(String txfs,String xzqxm, String zddz, ArrayList mnlxx)
		throws Exception;
    
    public   String sendZdygzdnlcdyxsjcspz(String txfs,String xzqxm, String zddz, String[][] cs)
		throws Exception;
    
    public   String sendSygjbz(String txfs,String xzqxm, String zddz, String sygjbz)
		throws Exception;
    
    public   String sendXbxz(String txfs,String xzqxm, String zddz, String[][] xbxz)
		throws Exception;
    
    public   String sendCldxzcs(String txfs,String xzqxm, String zddz, String cldh,
      		String dyhglsx,String dyhglxx,String dydxmx,String gymx,
    		String qymx,String glmx,String eddlmx,String lxdlsx,String szglssx,
    		String szglsx,String sxdybphxz,String sxdlbphxz,String lxsysjxz)
		throws Exception;
    
    public   String sendCldglysfdxz(String txfs,String xzqxm, String zddz, String cldh,
      		String xz1,String xz2)
		throws Exception;
    
    public   String sendZwxx(String txfs,String xzqxm,String zddz,String zl,String bh,String hzxx)
		throws Exception;
    
    public   String sendDrqkztrbz(String txfs,String xzqxm,String zddz,String cldh,
  			String trbz,String drqz)
		throws Exception;
    
    public  String sendAFN0FF1(String txfs,String xzqxm, String zddz, 
			  String csz) throws Exception ;
    
    public  String sendAFN0FF2(String txfs,String xzqxm, String zddz, 
			  String csz) throws Exception ;
    
    public  String updateSingle(String txfs,String xzqxm, String zddz, 
			  String fileName) throws Exception ;
}