<?php

use Twig\Environment;
use Twig\Error\LoaderError;
use Twig\Error\RuntimeError;
use Twig\Markup;
use Twig\Sandbox\SecurityError;
use Twig\Sandbox\SecurityNotAllowedTagError;
use Twig\Sandbox\SecurityNotAllowedFilterError;
use Twig\Sandbox\SecurityNotAllowedFunctionError;
use Twig\Source;
use Twig\Template;

/* themes/custom/canada_experiments/templates/html/page--gcweb.html.twig */
class __TwigTemplate_5f2c72577bcf31743003b870802befe504795288238b3aaa60e9aff10fb16c35 extends \Twig\Template
{
    public function __construct(Environment $env)
    {
        parent::__construct($env);

        $this->parent = false;

        $this->blocks = [
            'navbar' => [$this, 'block_navbar'],
            'main' => [$this, 'block_main'],
            'highlighted' => [$this, 'block_highlighted'],
            'header' => [$this, 'block_header'],
            'breadcrumb' => [$this, 'block_breadcrumb'],
            'action_links' => [$this, 'block_action_links'],
            'help' => [$this, 'block_help'],
            'content' => [$this, 'block_content'],
            'content_suffix' => [$this, 'block_content_suffix'],
            'sidebar_first' => [$this, 'block_sidebar_first'],
            'sidebar_second' => [$this, 'block_sidebar_second'],
            'footer' => [$this, 'block_footer'],
        ];
        $this->sandbox = $this->env->getExtension('\Twig\Extension\SandboxExtension');
        $tags = ["set" => 59, "if" => 62, "block" => 63];
        $filters = ["t" => 216, "clean_class" => 69];
        $functions = [];

        try {
            $this->sandbox->checkSecurity(
                ['set', 'if', 'block'],
                ['t', 'clean_class'],
                []
            );
        } catch (SecurityError $e) {
            $e->setSourceContext($this->getSourceContext());

            if ($e instanceof SecurityNotAllowedTagError && isset($tags[$e->getTagName()])) {
                $e->setTemplateLine($tags[$e->getTagName()]);
            } elseif ($e instanceof SecurityNotAllowedFilterError && isset($filters[$e->getFilterName()])) {
                $e->setTemplateLine($filters[$e->getFilterName()]);
            } elseif ($e instanceof SecurityNotAllowedFunctionError && isset($functions[$e->getFunctionName()])) {
                $e->setTemplateLine($functions[$e->getFunctionName()]);
            }

            throw $e;
        }

    }

    protected function doDisplay(array $context, array $blocks = [])
    {
        // line 59
        $context["container"] = (($this->getAttribute($this->getAttribute(($context["theme"] ?? null), "settings", []), "fluid_container", [])) ? ("container-fluid") : ("container"));
        // line 60
        echo "
";
        // line 62
        if (($this->getAttribute(($context["page"] ?? null), "navigation", []) || $this->getAttribute(($context["page"] ?? null), "navigation_collapsible", []))) {
            // line 63
            echo "  ";
            $this->displayBlock('navbar', $context, $blocks);
        }
        // line 94
        echo "
";
        // line 96
        $this->displayBlock('main', $context, $blocks);
        // line 212
        echo "
";
        // line 214
        if ((($context["gcweb_cdn_goc"] ?? null) &&  !($context["gcweb_election"] ?? null))) {
            // line 215
            echo "  <aside class=\"gc-nttvs ";
            echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed(($context["container"] ?? null)), "html", null, true);
            echo "\">
    <h2>";
            // line 216
            echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->renderVar(t("Government of Canada activities and initiatives"));
            echo "</h2>
    <div id=\"gcwb_prts\" class=\"wb-eqht row\" data-ajax-replace=\"//cdn.canada.ca/gcweb-cdn-live/features/features-";
            // line 217
            echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed(($context["language"] ?? null)), "html", null, true);
            echo ".html\">
      <p class=\"mrgn-lft-md\">
        <a href=\"http://www.canada.ca/activities.html\">";
            // line 219
            echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->renderVar(t("Access Government of Canada activities and initiatives"));
            echo "</a>
      </p>
    </div>
  </aside>
";
        }
        // line 224
        echo "
";
        // line 225
        if ($this->getAttribute(($context["page"] ?? null), "footer", [])) {
            // line 226
            echo "  ";
            if (($context["gcweb_cdn_footer_enable"] ?? null)) {
                // line 227
                echo "    <footer id=\"wb-info\" data-ajax-replace=\"";
                echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed(($context["gcweb_cdn_footer_url"] ?? null)), "html", null, true);
                echo "\"></footer>
  ";
            } else {
                // line 229
                echo "    ";
                $this->displayBlock('footer', $context, $blocks);
                // line 251
                echo "  ";
            }
        }
        // line 253
        echo "

";
    }

    // line 63
    public function block_navbar($context, array $blocks = [])
    {
        // line 64
        echo "
    ";
        // line 66
        $context["navbar_classes"] = [0 => "navbar", 1 => (($this->getAttribute($this->getAttribute(        // line 68
($context["theme"] ?? null), "settings", []), "navbar_inverse", [])) ? ("navbar-inverse") : ("navbar-default")), 2 => (($this->getAttribute($this->getAttribute(        // line 69
($context["theme"] ?? null), "settings", []), "navbar_position", [])) ? (("navbar-" . \Drupal\Component\Utility\Html::getClass($this->sandbox->ensureToStringAllowed($this->getAttribute($this->getAttribute(($context["theme"] ?? null), "settings", []), "navbar_position", []))))) : (""))];
        // line 72
        echo "    <header";
        echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute(($context["navbar_attributes"] ?? null), "addClass", [0 => ($context["navbar_classes"] ?? null)], "method")), "html", null, true);
        echo " id=\"navbar\">
      <div id=\"wb-bnr\" class=\"";
        // line 73
        echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed(($context["container"] ?? null)), "html", null, true);
        echo "\">
        <section id=\"wb-lng\" class=\"text-right\">
          <h2 class=\"wb-inv\">";
        // line 75
        echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->renderVar(t("Language selection"));
        echo "</h2>
          ";
        // line 76
        echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute(($context["page"] ?? null), "language_toggle", [])), "html", null, true);
        echo "
        </section>
        <div class=\"row\">
          ";
        // line 79
        echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute(($context["page"] ?? null), "banner", [])), "html", null, true);
        echo "
          ";
        // line 80
        echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute(($context["page"] ?? null), "search", [])), "html", null, true);
        echo "
        </div>
      </div>
      <nav class=\"gcweb-menu\" data-trgt=\"mb-pnl\" typeof=\"SiteNavigationElement\">
        <div class=\"";
        // line 84
        echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed(($context["container"] ?? null)), "html", null, true);
        echo "\">
          <h2 class=\"wb-inv\">";
        // line 85
        echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->renderVar(t("Menu"));
        echo "</h2>
          <button type=\"button\" aria-haspopup=\"true\" aria-expanded=\"false\"><span class=\"wb-inv\">Main </span>Menu <span class=\"expicon glyphicon glyphicon-chevron-down\"></span></button>
          ";
        // line 87
        echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute(($context["page"] ?? null), "navigation", [])), "html", null, true);
        echo "
        </div>
      </nav>
      ";
        // line 90
        echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute(($context["page"] ?? null), "breadcrumb", [])), "html", null, true);
        echo "
    </header>
  ";
    }

    // line 96
    public function block_main($context, array $blocks = [])
    {
        // line 97
        echo "
  <div class=\"";
        // line 98
        echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, ((($context["is_front"] ?? null)) ? ("container-fluid") : (($context["container"] ?? null))), "html", null, true);
        echo "\">
    <div class=\"row\">

      ";
        // line 102
        echo "      ";
        if ($this->getAttribute(($context["page"] ?? null), "highlighted", [])) {
            // line 103
            echo "        ";
            $this->displayBlock('highlighted', $context, $blocks);
            // line 106
            echo "      ";
        }
        // line 107
        echo "
      ";
        // line 109
        echo "      ";
        // line 110
        $context["content_classes"] = [0 => ((($this->getAttribute(        // line 111
($context["page"] ?? null), "sidebar_first", []) && $this->getAttribute(($context["page"] ?? null), "sidebar_second", []))) ? ("col-md-6 col-md-push-3") : ("")), 1 => ((($this->getAttribute(        // line 112
($context["page"] ?? null), "sidebar_first", []) && twig_test_empty($this->getAttribute(($context["page"] ?? null), "sidebar_second", [])))) ? ("col-md-9 col-md-push-3") : ("")), 2 => ((($this->getAttribute(        // line 113
($context["page"] ?? null), "sidebar_second", []) && twig_test_empty($this->getAttribute(($context["page"] ?? null), "sidebar_first", [])))) ? ("col-md-9") : ("")), 3 => (((twig_test_empty($this->getAttribute(        // line 114
($context["page"] ?? null), "sidebar_first", [])) && twig_test_empty($this->getAttribute(($context["page"] ?? null), "sidebar_second", [])))) ? ("col-md-12") : (""))];
        // line 117
        echo "      <main role=\"main\" property=\"mainContentOfPage\" ";
        echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute(($context["content_attributes"] ?? null), "addClass", [0 => ($context["content_classes"] ?? null), 1 => "main-container", 2 => ($context["container"] ?? null), 3 => "js-quickedit-main-content"], "method")), "html", null, true);
        echo ">

      ";
        // line 120
        echo "      ";
        if ($this->getAttribute(($context["page"] ?? null), "header", [])) {
            // line 121
            echo "        ";
            $this->displayBlock('header', $context, $blocks);
            // line 124
            echo "      ";
        }
        // line 125
        echo "
      <section>

        ";
        // line 129
        echo "        ";
        if (($context["breadcrumb"] ?? null)) {
            // line 130
            echo "          ";
            $this->displayBlock('breadcrumb', $context, $blocks);
            // line 133
            echo "        ";
        }
        // line 134
        echo "
        ";
        // line 136
        echo "        ";
        if (($context["action_links"] ?? null)) {
            // line 137
            echo "          ";
            $this->displayBlock('action_links', $context, $blocks);
            // line 140
            echo "        ";
        }
        // line 141
        echo "
        ";
        // line 143
        echo "        ";
        if ($this->getAttribute(($context["page"] ?? null), "help", [])) {
            // line 144
            echo "          ";
            $this->displayBlock('help', $context, $blocks);
            // line 147
            echo "        ";
        }
        // line 148
        echo "
        ";
        // line 150
        echo "        ";
        $this->displayBlock('content', $context, $blocks);
        // line 170
        echo "      </section>

      </main>

      ";
        // line 175
        echo "      ";
        // line 176
        $context["sidebar_first_classes"] = [0 => ((($this->getAttribute(        // line 177
($context["page"] ?? null), "sidebar_first", []) && $this->getAttribute(($context["page"] ?? null), "sidebar_second", []))) ? ("col-md-3 col-md-pull-6") : ("")), 1 => ((($this->getAttribute(        // line 178
($context["page"] ?? null), "sidebar_first", []) && twig_test_empty($this->getAttribute(($context["page"] ?? null), "sidebar_second", [])))) ? ("col-md-3 col-md-pull-9") : ("")), 2 => ((($this->getAttribute(        // line 179
($context["page"] ?? null), "sidebar_second", []) && twig_test_empty($this->getAttribute(($context["page"] ?? null), "sidebar_first", [])))) ? ("col-md-3 col-md-pull-9") : (""))];
        // line 182
        echo "      ";
        // line 183
        echo "      ";
        if ($this->getAttribute(($context["page"] ?? null), "sidebar_first", [])) {
            // line 184
            echo "        ";
            $this->displayBlock('sidebar_first', $context, $blocks);
            // line 189
            echo "      ";
        }
        // line 190
        echo "
      ";
        // line 192
        echo "      ";
        // line 193
        $context["sidebar_second_classes"] = [0 => ((($this->getAttribute(        // line 194
($context["page"] ?? null), "sidebar_first", []) && $this->getAttribute(($context["page"] ?? null), "sidebar_second", []))) ? ("col-md-3") : ("")), 1 => ((($this->getAttribute(        // line 195
($context["page"] ?? null), "sidebar_first", []) && twig_test_empty($this->getAttribute(($context["page"] ?? null), "sidebar_second", [])))) ? ("col-md-3") : ("")), 2 => ((($this->getAttribute(        // line 196
($context["page"] ?? null), "sidebar_second", []) && twig_test_empty($this->getAttribute(($context["page"] ?? null), "sidebar_first", [])))) ? ("col-md-3") : (""))];
        // line 199
        echo "      ";
        // line 200
        echo "      ";
        if ($this->getAttribute(($context["page"] ?? null), "sidebar_second", [])) {
            // line 201
            echo "        ";
            $this->displayBlock('sidebar_second', $context, $blocks);
            // line 206
            echo "      ";
        }
        // line 207
        echo "
    </div>
  </div>

";
    }

    // line 103
    public function block_highlighted($context, array $blocks = [])
    {
        // line 104
        echo "          <div class=\"highlighted\">";
        echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute(($context["page"] ?? null), "highlighted", [])), "html", null, true);
        echo "</div>
        ";
    }

    // line 121
    public function block_header($context, array $blocks = [])
    {
        // line 122
        echo "          ";
        echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute(($context["page"] ?? null), "header", [])), "html", null, true);
        echo "
        ";
    }

    // line 130
    public function block_breadcrumb($context, array $blocks = [])
    {
        // line 131
        echo "            ";
        echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed(($context["breadcrumb"] ?? null)), "html", null, true);
        echo "
          ";
    }

    // line 137
    public function block_action_links($context, array $blocks = [])
    {
        // line 138
        echo "            <ul class=\"action-links\">";
        echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed(($context["action_links"] ?? null)), "html", null, true);
        echo "</ul>
          ";
    }

    // line 144
    public function block_help($context, array $blocks = [])
    {
        // line 145
        echo "            ";
        echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute(($context["page"] ?? null), "help", [])), "html", null, true);
        echo "
          ";
    }

    // line 150
    public function block_content($context, array $blocks = [])
    {
        // line 151
        echo "          <a id=\"main-content\"></a>
          ";
        // line 152
        echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute(($context["page"] ?? null), "content", [])), "html", null, true);
        echo "

          ";
        // line 155
        echo "          ";
        if ($this->getAttribute(($context["page"] ?? null), "content_suffix", [])) {
            // line 156
            echo "            ";
            $this->displayBlock('content_suffix', $context, $blocks);
            // line 163
            echo "          ";
        }
        // line 164
        echo "

          ";
        // line 166
        echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute(($context["page"] ?? null), "content_footer", [])), "html", null, true);
        echo "
          

        ";
    }

    // line 156
    public function block_content_suffix($context, array $blocks = [])
    {
        // line 157
        echo "              <div class=\"content-suffix\">
                <div class=\"container\">
                  <div class=\"row\">";
        // line 159
        echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute(($context["page"] ?? null), "content_suffix", [])), "html", null, true);
        echo "</div>
                </div>
              </div>
            ";
    }

    // line 184
    public function block_sidebar_first($context, array $blocks = [])
    {
        // line 185
        echo "          <nav";
        echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute(($context["attributes"] ?? null), "addClass", [0 => ($context["sidebar_first_classes"] ?? null)], "method")), "html", null, true);
        echo ">
            ";
        // line 186
        echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute(($context["page"] ?? null), "sidebar_first", [])), "html", null, true);
        echo "
          </nav>
        ";
    }

    // line 201
    public function block_sidebar_second($context, array $blocks = [])
    {
        // line 202
        echo "          <nav";
        echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute($this->getAttribute(($context["attributes"] ?? null), "removeClass", [0 => ($context["sidebar_first_classes"] ?? null)], "method"), "addClass", [0 => ($context["sidebar_second_classes"] ?? null)], "method")), "html", null, true);
        echo ">
            ";
        // line 203
        echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute(($context["page"] ?? null), "sidebar_second", [])), "html", null, true);
        echo "
          </nav>
        ";
    }

    // line 229
    public function block_footer($context, array $blocks = [])
    {
        // line 230
        echo "      <footer id=\"wb-info\">
        <div class=\"landscape\">
          <div class=\"";
        // line 232
        echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed(($context["container"] ?? null)), "html", null, true);
        echo "\">
            ";
        // line 233
        echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute(($context["page"] ?? null), "footer", [])), "html", null, true);
        echo "
          </div> 
        </div>
        <div class=\"brand\">
          <div class=\"";
        // line 237
        echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed(($context["container"] ?? null)), "html", null, true);
        echo "\">
            <div class=\"row \">
              ";
        // line 239
        echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute(($context["page"] ?? null), "branding", [])), "html", null, true);
        echo "
              <div class=\"col-xs-6 visible-sm visible-xs tofpg\">
                <a href=\"#wb-cont\">";
        // line 241
        echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->renderVar(t("Top of Page"));
        echo "<span class=\"glyphicon glyphicon-chevron-up\"></span></a>
              </div>
              <div class=\"col-xs-6 col-md-2 text-right\">
                <img src='";
        // line 244
        echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed(($context["logo_bottom_svg"] ?? null)), "html", null, true);
        echo "' alt='";
        echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->renderVar(t("Symbol of the Government of Canada"));
        echo "' />
              </div>
            </div>
          </div>
        </div>
      </footer>
    ";
    }

    public function getTemplateName()
    {
        return "themes/custom/canada_experiments/templates/html/page--gcweb.html.twig";
    }

    public function isTraitable()
    {
        return false;
    }

    public function getDebugInfo()
    {
        return array (  505 => 244,  499 => 241,  494 => 239,  489 => 237,  482 => 233,  478 => 232,  474 => 230,  471 => 229,  464 => 203,  459 => 202,  456 => 201,  449 => 186,  444 => 185,  441 => 184,  433 => 159,  429 => 157,  426 => 156,  418 => 166,  414 => 164,  411 => 163,  408 => 156,  405 => 155,  400 => 152,  397 => 151,  394 => 150,  387 => 145,  384 => 144,  377 => 138,  374 => 137,  367 => 131,  364 => 130,  357 => 122,  354 => 121,  347 => 104,  344 => 103,  336 => 207,  333 => 206,  330 => 201,  327 => 200,  325 => 199,  323 => 196,  322 => 195,  321 => 194,  320 => 193,  318 => 192,  315 => 190,  312 => 189,  309 => 184,  306 => 183,  304 => 182,  302 => 179,  301 => 178,  300 => 177,  299 => 176,  297 => 175,  291 => 170,  288 => 150,  285 => 148,  282 => 147,  279 => 144,  276 => 143,  273 => 141,  270 => 140,  267 => 137,  264 => 136,  261 => 134,  258 => 133,  255 => 130,  252 => 129,  247 => 125,  244 => 124,  241 => 121,  238 => 120,  232 => 117,  230 => 114,  229 => 113,  228 => 112,  227 => 111,  226 => 110,  224 => 109,  221 => 107,  218 => 106,  215 => 103,  212 => 102,  206 => 98,  203 => 97,  200 => 96,  193 => 90,  187 => 87,  182 => 85,  178 => 84,  171 => 80,  167 => 79,  161 => 76,  157 => 75,  152 => 73,  147 => 72,  145 => 69,  144 => 68,  143 => 66,  140 => 64,  137 => 63,  131 => 253,  127 => 251,  124 => 229,  118 => 227,  115 => 226,  113 => 225,  110 => 224,  102 => 219,  97 => 217,  93 => 216,  88 => 215,  86 => 214,  83 => 212,  81 => 96,  78 => 94,  74 => 63,  72 => 62,  69 => 60,  67 => 59,);
    }

    /** @deprecated since 1.27 (to be removed in 2.0). Use getSourceContext() instead */
    public function getSource()
    {
        @trigger_error('The '.__METHOD__.' method is deprecated since version 1.27 and will be removed in 2.0. Use getSourceContext() instead.', E_USER_DEPRECATED);

        return $this->getSourceContext()->getCode();
    }

    public function getSourceContext()
    {
        return new Source("{#
/**
 * @file
 * Default theme implementation to display a single page.
 *
 * The doctype, html, head and body tags are not in this template. Instead they
 * can be found in the html.html.twig template in this directory.
 *
 * Available variables:
 *
 * General utility variables:
 * - base_path: The base URL path of the Drupal installation. Will usually be
 *   \"/\" unless you have installed Drupal in a sub-directory.
 * - is_front: A flag indicating if the current page is the front page.
 * - logged_in: A flag indicating if the user is registered and signed in.
 * - is_admin: A flag indicating if the user has permission to access
 *   administration pages.
 *
 * Site identity:
 * - front_page: The URL of the front page. Use this instead of base_path when
 *   linking to the front page. This includes the language domain or prefix.
 *
 * Navigation:
 * - breadcrumb: The breadcrumb trail for the current page.
 *
 * Page content (in order of occurrence in the default page.html.twig):
 * - title_prefix: Additional output populated by modules, intended to be
 *   displayed in front of the main title tag that appears in the template.
 * - title: The page title, for use in the actual content.
 * - title_suffix: Additional output populated by modules, intended to be
 *   displayed after the main title tag that appears in the template.
 * - messages: Status and error messages. Should be displayed prominently.
 * - tabs: Tabs linking to any sub-pages beneath the current page (e.g., the
 *   view and edit tabs when displaying a node).
 * - action_links: Actions local to the page, such as \"Add menu\" on the menu
 *   administration interface.
 * - node: Fully loaded node, if there is an automatically-loaded node
 *   associated with the page and the node ID is the second argument in the
 *   page's path (e.g. node/12345 and node/12345/revisions, but not
 *   comment/reply/12345).
 *
 * Regions:
 * - page.header: Items for the header region.
 * - page.navigation: Items for the navigation region.
 * - page.navigation_collapsible: Items for the navigation (collapsible) region.
 * - page.highlighted: Items for the highlighted content region.
 * - page.help: Dynamic help text, mostly for admin pages.
 * - page.content: The main content of the current page.
 * - page.sidebar_first: Items for the first sidebar.
 * - page.sidebar_second: Items for the second sidebar.
 * - page.footer: Items for the footer region.
 *
 * @ingroup templates
 *
 * @see template_preprocess_page()
 * @see html.html.twig
 */
#}
{% set container = theme.settings.fluid_container ? 'container-fluid' : 'container' %}

{# Navbar #}
{% if page.navigation or page.navigation_collapsible %}
  {% block navbar %}

    {%
      set navbar_classes = [
        'navbar',
        theme.settings.navbar_inverse ? 'navbar-inverse' : 'navbar-default',
        theme.settings.navbar_position ? 'navbar-' ~ theme.settings.navbar_position|clean_class : '',
      ]
    %}
    <header{{ navbar_attributes.addClass(navbar_classes) }} id=\"navbar\">
      <div id=\"wb-bnr\" class=\"{{ container }}\">
        <section id=\"wb-lng\" class=\"text-right\">
          <h2 class=\"wb-inv\">{{ 'Language selection'|t }}</h2>
          {{ page.language_toggle }}
        </section>
        <div class=\"row\">
          {{ page.banner }}
          {{ page.search }}
        </div>
      </div>
      <nav class=\"gcweb-menu\" data-trgt=\"mb-pnl\" typeof=\"SiteNavigationElement\">
        <div class=\"{{ container }}\">
          <h2 class=\"wb-inv\">{{ 'Menu'|t }}</h2>
          <button type=\"button\" aria-haspopup=\"true\" aria-expanded=\"false\"><span class=\"wb-inv\">Main </span>Menu <span class=\"expicon glyphicon glyphicon-chevron-down\"></span></button>
          {{ page.navigation }}
        </div>
      </nav>
      {{ page.breadcrumb }}
    </header>
  {% endblock %}
{% endif %}

{# Main #}
{% block main %}

  <div class=\"{{ is_front ? \"container-fluid\" : container }}\">
    <div class=\"row\">

      {# Highlighted #}
      {% if page.highlighted %}
        {% block highlighted  %}
          <div class=\"highlighted\">{{ page.highlighted }}</div>
        {% endblock %}
      {% endif %}

      {# Content #}
      {%
        set content_classes = [
          page.sidebar_first and page.sidebar_second ? 'col-md-6 col-md-push-3',
          page.sidebar_first and page.sidebar_second is empty ? 'col-md-9 col-md-push-3',
          page.sidebar_second and page.sidebar_first is empty ? 'col-md-9',
          page.sidebar_first is empty and page.sidebar_second is empty ? 'col-md-12'
        ]
      %}
      <main role=\"main\" property=\"mainContentOfPage\" {{ content_attributes.addClass(content_classes, 'main-container', container, 'js-quickedit-main-content') }}>

      {# Header #}
      {% if page.header %}
        {% block header %}
          {{ page.header }}
        {% endblock %}
      {% endif %}

      <section>

        {# Breadcrumbs #}
        {% if breadcrumb %}
          {% block breadcrumb %}
            {{ breadcrumb }}
          {% endblock %}
        {% endif %}

        {# Action Links #}
        {% if action_links %}
          {% block action_links %}
            <ul class=\"action-links\">{{ action_links }}</ul>
          {% endblock %}
        {% endif %}

        {# Help #}
        {% if page.help %}
          {% block help %}
            {{ page.help }}
          {% endblock %}
        {% endif %}

        {# Content #}
        {% block content %}
          <a id=\"main-content\"></a>
          {{ page.content }}

          {# Content suffix #}
          {% if page.content_suffix %}
            {% block content_suffix %}
              <div class=\"content-suffix\">
                <div class=\"container\">
                  <div class=\"row\">{{ page.content_suffix }}</div>
                </div>
              </div>
            {% endblock %}
          {% endif %}


          {{ page.content_footer }}
          

        {% endblock %}
      </section>

      </main>

      {# Sidebar First #}
      {%
        set sidebar_first_classes = [
          page.sidebar_first and page.sidebar_second ? 'col-md-3 col-md-pull-6',
          page.sidebar_first and page.sidebar_second is empty ? 'col-md-3 col-md-pull-9',
          page.sidebar_second and page.sidebar_first is empty ? 'col-md-3 col-md-pull-9'
        ]
      %}
      {# Sidebar First #}
      {% if page.sidebar_first %}
        {% block sidebar_first %}
          <nav{{ attributes.addClass(sidebar_first_classes) }}>
            {{ page.sidebar_first }}
          </nav>
        {% endblock %}
      {% endif %}

      {# Sidebar Second #}
      {%
        set sidebar_second_classes = [
          page.sidebar_first and page.sidebar_second ? 'col-md-3',
          page.sidebar_first and page.sidebar_second is empty ? 'col-md-3',
          page.sidebar_second and page.sidebar_first is empty ? 'col-md-3'
        ]
      %}
      {# Sidebar Second #}
      {% if page.sidebar_second %}
        {% block sidebar_second %}
          <nav{{ attributes.removeClass(sidebar_first_classes).addClass(sidebar_second_classes) }}>
            {{ page.sidebar_second }}
          </nav>
        {% endblock %}
      {% endif %}

    </div>
  </div>

{% endblock %}

{# GCWeb #}
{% if gcweb_cdn_goc and not gcweb_election %}
  <aside class=\"gc-nttvs {{ container }}\">
    <h2>{{ 'Government of Canada activities and initiatives'|t }}</h2>
    <div id=\"gcwb_prts\" class=\"wb-eqht row\" data-ajax-replace=\"//cdn.canada.ca/gcweb-cdn-live/features/features-{{ language }}.html\">
      <p class=\"mrgn-lft-md\">
        <a href=\"http://www.canada.ca/activities.html\">{{ 'Access Government of Canada activities and initiatives'|t }}</a>
      </p>
    </div>
  </aside>
{% endif %}

{% if page.footer %}
  {% if gcweb_cdn_footer_enable %}
    <footer id=\"wb-info\" data-ajax-replace=\"{{ gcweb_cdn_footer_url }}\"></footer>
  {% else %}
    {% block footer %}
      <footer id=\"wb-info\">
        <div class=\"landscape\">
          <div class=\"{{ container }}\">
            {{ page.footer }}
          </div> 
        </div>
        <div class=\"brand\">
          <div class=\"{{ container }}\">
            <div class=\"row \">
              {{ page.branding }}
              <div class=\"col-xs-6 visible-sm visible-xs tofpg\">
                <a href=\"#wb-cont\">{{ 'Top of Page'|t }}<span class=\"glyphicon glyphicon-chevron-up\"></span></a>
              </div>
              <div class=\"col-xs-6 col-md-2 text-right\">
                <img src='{{ logo_bottom_svg }}' alt='{{ 'Symbol of the Government of Canada'|t }}' />
              </div>
            </div>
          </div>
        </div>
      </footer>
    {% endblock %}
  {% endif %}
{% endif %}


", "themes/custom/canada_experiments/templates/html/page--gcweb.html.twig", "/var/www/html/tbs-proto1.openplus.ca/themes/custom/canada_experiments/templates/html/page--gcweb.html.twig");
    }
}
