package ca.tbssct.ep;
public class GitMain {

	public static void main(String args[]) throws Exception {
		System.out.println(Util.ExecuteCommand("/home/hyma/dns/", "git branch"));
		System.out.println(Util.ExecuteCommand("/home/hyma/dns/", "git checkout platform-alpha-canada-ca"));
		System.out.println(Util.ExecuteCommand("/home/hyma/dns/", "git branch"));
		String templateDNS = Util.fileToString("/home/hyma/ExperimentalPlatform/templates/terraformdns.tf");
		templateDNS = templateDNS.replace("#ip#", "127.1.1.123").replace("#dnsprefix#", "drupal1234");
		Util.writeFile("/home/hyma/dns/terraform/drupal1234dddd.alpha.canada.ca.tk", templateDNS);
		System.out.println(Util.ExecuteCommand("/home/hyma/dns/", "git add -A"));
		System.out.println(Util.ExecuteCommand("/home/hyma/dns/", "git commit -m \"Adding drupal1234 DNS entry\""));
		System.out.println(
				Util.ExecuteCommand("/home/hyma/dns/", "git push --set-upstream origin platform-alpha-canada-ca"));
		System.out.println(
				Util.ExecuteCommand("/home/hyma/dns/", "hub pull-request platform-alpha-canada-ca -m \"Automatic GitHub pull request\\n\\n Please review and accept\""));

	}
}
