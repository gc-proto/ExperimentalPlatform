SELECT 
-- Fields mapped with same name under Solr.

    AWR.awr_id                 as id,
    AWR.awr_unified_no         as recall_no,
    AWR.awr_date               as recall_date,
    INF.title                  as recall_title,
    DPT.name                   as recall_dept,
    GROUP_CONCAT(
        DISTINCT AU.audience 
        ORDER BY AU.audience_code 
        separator '|')         as recall_audiences,
    GROUP_CONCAT(
        DISTINCT CAT.name
        ORDER BY CAT.category_code 
        separator '|')         as recall_categories,
    ALT.name                   as recall_alert_type,

-- "recall_desc" fields used for dynamic summaries.

    INF.summary_product_name   as info_summary_product_name,
    INF.summary_issue          as info_summary_issue,
    INF.summary_what_to_do     as info_summary_what_to_do,
    INF.consumer_action        as info_consumer_action,
    INF.subtitle               as info_subtitle,
    INF.intro_text             as info_intro_text,
    INF.awr_reason             as info_awr_reason,
    INF.full_awr_text          as info_full_awr_text,
    PDT.trade_name             as info_trade_name,
    PDT.code_on_product        as info_sku,
    PDT.common_name            as info_common_name,
    PDT.additional_name        as info_additional_name,

-- Others: used for computed fields or "catch all" Solr fields.

    AWR.manufacturer_recall_no as manufacturer_recall_no,
    AWR.unit_affected_nb       as units_affected,
    AWR.live_due_date          as live_date,
    AWR.date_expired           as expired_date,
    AWR.date_last_update       as updated_date,
    AWR.date_created           as created_date,
    NTC.notification_text      as notification_type,
    AC.awr_class_name          as class_name,
    VS.system_name             as vehicle_system,
    ALS.name                   as alert_subtype,
    INF.background             as info_background,
    INF.enquiries              as info_enquiries,
    INF.feedback_request       as info_feedback_request,
    INF.for_more_information   as info_for_more_information,
    INF.goc_action             as info_goc_action,
    INF.goc_action_title       as info_goc_action_title,
    INF.report_problems        as info_report_problems,
    INF.side_effects           as info_side_effects,
    INF.source                 as info_source,
    INF.who_affected           as info_who_affected,
    INF.subtitle_extra         as info_subtitle_extra,
    INF.description            as info_description,
    INF.problem_issue          as info_problem_issue,
    INF.tags                   as info_tags,
    INF.unit_affected_text     as info_unit_affected_text,
    INF.depth_distribution     as info_depth_distribution,
    INF.industry_action        as info_industry_action,
    INF.logo_sponsor           as info_logo_sponsor,
    INF.media_enquiries        as info_media_enquiries,
    INF.public_enquiries       as info_public_enquiries,
    INF.related_content        as info_related_content,
    INF.summary_audience       as info_summary_audience,
    PDT.code_on_product        as info_code_on_product,
    PDT.lot_serial_number      as info_lot_serial_number,
    PDT.model_catalog_number   as info_model_catalog_number,
    PDT.product_description    as info_product_description,
    PDT.rss_product_desc       as info_rss_product_desc,
    PDT.size                   as info_size,
    PDT.time_period            as info_time_period,
    PDT.units_involved         as info_units_involved,
    PDT.hazard                 as info_hazard,
    PDT.distribution_details   as info_distribution_details,
    PDT.depth_details          as info_depth_details,
    PDT.additional_information as info_additional_information,
    PA.upc                     as product_upc,
    DOC.title                  as doc_title,
    DOC.description            as doc_desc,
    DOC.long_description       as doc_long_desc,
    DOC.comments               as doc_comments,
    DOC.url                    as img_url,
    DOC.thumbnail_url          as img_thumbnail_url,
    DOC.mobile_url             as img_mobile_url,
    DOC.alternate_text         as img_alt_text,
    DR.name                    as reason,
    O.manufacturer_place_of_origin as origin,
    DRG.din_npn_din_hm         as drug_din,
    DRG.dosage                 as drug_dosage,
    DRG.strength               as drug_strength,
    DD.depth_text              as distribution_depth,
    GD.dist_text               as distribution_geo,
    ROL.role                   as cie_role,
    MAK.make_name              as cie_make,
    MDL.model_name             as cie_model,
    GROUP_CONCAT(
        DISTINCT MAN.year 
        ORDER BY MAN.year desc 
        separator '|')         as cie_years,
    CIE.company_name           as cie_name,
    CPR.name                   as provider_name,
    CPR.acronym                as provider_acronym,
    BG.name                    as branch_name,
    BG.acronym                 as branch_acronym,
    DPT.acronym                as department_acronym,
    II.description             as incl_description
FROM
    awr AWR
LEFT JOIN notification_type NTC
    ON AWR.notification_type_code = NTC.notification_type_code
    AND NTC.language = 1
LEFT JOIN awr_class AC
    ON AWR.awr_class_code = AC.awr_class_code
    AND AC.language = 1
LEFT JOIN vehicle_system VS
    ON AWR.vehicle_system_code = VS.vehicle_system_code
    AND VS.language = 1
LEFT JOIN awr_alert AL
    ON AWR.awr_alert_id = AL.awr_alert_id
    LEFT JOIN alert_type ALT
        ON AL.alert_type_code = ALT.alert_type_code
        AND ALT.language = 1
    LEFT JOIN alert_subtype ALS
        ON AL.alert_subtype_code = ALS.alert_subtype_code
        AND ALS.language = 1
LEFT JOIN awr_audience AUD
    ON AWR.awr_id = AUD.awr_id
    LEFT JOIN audience AU
        ON AUD.audience_code = AU.audience_code
        AND AU.language = 1
LEFT JOIN awr_category ACA
    ON AWR.awr_id = ACA.awr_id
    LEFT JOIN category CAT
        ON ACA.category_code = CAT.category_code
        AND CAT.language = 1
LEFT JOIN awr_additional_info INF
    ON AWR.awr_id  = INF.awr_id
    AND INF.language = 1
LEFT JOIN products_affected PA
    ON AWR.awr_id  = PA.awr_id
    LEFT JOIN products_affected_documents PDO
        ON PA.product_affected_id = PDO.product_affected_id
            LEFT JOIN document DOC
                ON PDO.document_code = DOC.document_code
                AND DOC.language = 1
    LEFT JOIN products_affected_reason PAR
        ON PA.product_affected_id = PAR.product_affected_id
            LEFT JOIN detailed_reason DR
                ON PAR.detailed_reason_code = DR.detailed_reason_code
                AND DR.language = 1
    LEFT JOIN origin O
        ON PA.product_affected_id = O.product_affected_id
        AND O.language = 1
    LEFT JOIN products_affected_details PDT
        ON PA.product_affected_id = PDT.product_affected_id
        AND PDT.language = 1
    LEFT JOIN drug DRG
        ON PA.product_affected_id = DRG.product_affected_id
        AND DRG.language = 1
    LEFT JOIN products_affected_depth_distribution PDD
        ON PA.product_affected_id = PDD.product_affected_id
            LEFT JOIN depth_distribution DD
                ON PDD.depth_distribution_code = DD.depth_distribution_code
                AND DD.language = 1
    LEFT JOIN products_affected_geo_distributions PGD
        ON PA.product_affected_id = PGD.product_affected_id
            LEFT JOIN geo_distribution GD
                ON PGD.geo_distribution_code = GD.geo_distribution_code
                AND GD.language = 1
    LEFT JOIN products_affected_company PAC
        ON PA.product_affected_id = PAC.product_affected_id
            LEFT JOIN role ROL
                ON PAC.role_code = ROL.role_code
                AND ROL.language = 1
            LEFT JOIN make MAK
                ON PAC.make_code = MAK.make_code
                AND MAK.language = 1
            LEFT JOIN model MDL
                ON PAC.model_code = MDL.model_code
                AND MDL.language = 1
            LEFT JOIN manufacture MAN
                ON PAC.product_affected_company_id = MAN.product_affected_company_id
            LEFT JOIN company CIE
                ON PAC.company_code = CIE.company_code
                AND CIE.language = 1
LEFT JOIN content_provider CPR
    ON AWR.content_provider_id = CPR.content_provider_id
    AND CPR.language = 1
    LEFT JOIN branch_group BG
        ON CPR.branch_group_code = BG.branch_group_code
        AND BG.language = 1                
    LEFT JOIN department DPT
        ON CPR.department_code = DPT.department_code
        AND DPT.language = 1                
LEFT JOIN awr_include_information AII
    ON AWR.awr_id = AII.awr_id
    LEFT JOIN include_information II
        ON AII.include_info_code= II.include_info_code
        AND II.language = 1
        
GROUP BY AWR.awr_id