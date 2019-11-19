resource "aws_route53_record" "notification-alpha-canada-ca-A" {
    zone_id = "${aws_route53_zone.alpha-canada-ca-public.zone_id}"
    name    = "#dnsprefix#.alpha.canada.ca"
    type    = "A"
    records = [
        "#IP#"
    ]
    ttl     = "300"

}
