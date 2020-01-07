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

/* themes/contrib/wxt_bootstrap/templates/system/table.html.twig */
class __TwigTemplate_0d492c758804d26891fc854520966a75c95fa2296428fad76a372fe60b750017 extends \Twig\Template
{
    public function __construct(Environment $env)
    {
        parent::__construct($env);

        $this->parent = false;

        $this->blocks = [
        ];
        $this->sandbox = $this->env->getExtension('\Twig\Extension\SandboxExtension');
        $tags = ["if" => 48, "set" => 52, "for" => 66];
        $filters = [];
        $functions = ["cycle" => 98];

        try {
            $this->sandbox->checkSecurity(
                ['if', 'set', 'for'],
                [],
                ['cycle']
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
        // line 48
        if (($context["responsive"] ?? null)) {
            // line 49
            echo "  <div class=\"table-responsive\">
";
        }
        // line 52
        $context["classes"] = [0 => "table", 1 => ((        // line 54
($context["bordered"] ?? null)) ? ("table-bordered") : ("")), 2 => ((        // line 55
($context["condensed"] ?? null)) ? ("table-condensed") : ("")), 3 => ((        // line 56
($context["hover"] ?? null)) ? ("table-hover") : ("")), 4 => ((        // line 57
($context["striped"] ?? null)) ? ("table-striped") : ("")), 5 => ((        // line 58
($context["sticky"] ?? null)) ? ("sticky-enabled") : (""))];
        // line 61
        echo "<table";
        echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute(($context["attributes"] ?? null), "addClass", [0 => ($context["classes"] ?? null)], "method")), "html", null, true);
        echo ">
  ";
        // line 62
        if (($context["caption"] ?? null)) {
            // line 63
            echo "    <caption>";
            echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed(($context["caption"] ?? null)), "html", null, true);
            echo "</caption>
  ";
        }
        // line 65
        echo "
  ";
        // line 66
        $context['_parent'] = $context;
        $context['_seq'] = twig_ensure_traversable(($context["colgroups"] ?? null));
        foreach ($context['_seq'] as $context["_key"] => $context["colgroup"]) {
            // line 67
            echo "    ";
            if ($this->getAttribute($context["colgroup"], "cols", [])) {
                // line 68
                echo "      <colgroup";
                echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute($context["colgroup"], "attributes", [])), "html", null, true);
                echo ">
        ";
                // line 69
                $context['_parent'] = $context;
                $context['_seq'] = twig_ensure_traversable($this->getAttribute($context["colgroup"], "cols", []));
                foreach ($context['_seq'] as $context["_key"] => $context["col"]) {
                    // line 70
                    echo "          <col";
                    echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute($context["col"], "attributes", [])), "html", null, true);
                    echo " />
        ";
                }
                $_parent = $context['_parent'];
                unset($context['_seq'], $context['_iterated'], $context['_key'], $context['col'], $context['_parent'], $context['loop']);
                $context = array_intersect_key($context, $_parent) + $_parent;
                // line 72
                echo "      </colgroup>
    ";
            } else {
                // line 74
                echo "      <colgroup";
                echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute($context["colgroup"], "attributes", [])), "html", null, true);
                echo " />
    ";
            }
            // line 76
            echo "  ";
        }
        $_parent = $context['_parent'];
        unset($context['_seq'], $context['_iterated'], $context['_key'], $context['colgroup'], $context['_parent'], $context['loop']);
        $context = array_intersect_key($context, $_parent) + $_parent;
        // line 77
        echo "
  ";
        // line 78
        if (($context["header"] ?? null)) {
            // line 79
            echo "    <thead>
      <tr>
        ";
            // line 81
            $context['_parent'] = $context;
            $context['_seq'] = twig_ensure_traversable(($context["header"] ?? null));
            foreach ($context['_seq'] as $context["_key"] => $context["cell"]) {
                // line 82
                echo "        ";
                // line 83
                $context["cell_classes"] = [0 => (($this->getAttribute(                // line 84
$context["cell"], "active_table_sort", [])) ? ("active") : (""))];
                // line 87
                echo "        <";
                echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute($context["cell"], "tag", [])), "html", null, true);
                echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute($this->getAttribute($context["cell"], "attributes", []), "addClass", [0 => ($context["cell_classes"] ?? null)], "method")), "html", null, true);
                echo ">";
                // line 88
                echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute($context["cell"], "content", [])), "html", null, true);
                // line 89
                echo "</";
                echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute($context["cell"], "tag", [])), "html", null, true);
                echo ">
      ";
            }
            $_parent = $context['_parent'];
            unset($context['_seq'], $context['_iterated'], $context['_key'], $context['cell'], $context['_parent'], $context['loop']);
            $context = array_intersect_key($context, $_parent) + $_parent;
            // line 91
            echo "      </tr>
    </thead>
  ";
        }
        // line 94
        echo "
  ";
        // line 95
        if (($context["rows"] ?? null)) {
            // line 96
            echo "    <tbody>
      ";
            // line 97
            $context['_parent'] = $context;
            $context['_seq'] = twig_ensure_traversable(($context["rows"] ?? null));
            $context['loop'] = [
              'parent' => $context['_parent'],
              'index0' => 0,
              'index'  => 1,
              'first'  => true,
            ];
            if (is_array($context['_seq']) || (is_object($context['_seq']) && $context['_seq'] instanceof \Countable)) {
                $length = count($context['_seq']);
                $context['loop']['revindex0'] = $length - 1;
                $context['loop']['revindex'] = $length;
                $context['loop']['length'] = $length;
                $context['loop']['last'] = 1 === $length;
            }
            foreach ($context['_seq'] as $context["_key"] => $context["row"]) {
                // line 98
                echo "        ";
                $context["row_classes"] = [0 => twig_cycle([0 => "odd", 1 => "even"], $this->sandbox->ensureToStringAllowed($this->getAttribute($context["loop"], "index0", [])))];
                // line 99
                echo "        <tr";
                echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute($this->getAttribute($context["row"], "attributes", []), "addClass", [0 => ($context["row_classes"] ?? null)], "method")), "html", null, true);
                echo ">
          ";
                // line 100
                $context['_parent'] = $context;
                $context['_seq'] = twig_ensure_traversable($this->getAttribute($context["row"], "cells", []));
                foreach ($context['_seq'] as $context["_key"] => $context["cell"]) {
                    // line 101
                    echo "            <";
                    echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute($context["cell"], "tag", [])), "html", null, true);
                    echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute($context["cell"], "attributes", [])), "html", null, true);
                    echo ">";
                    // line 102
                    echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute($context["cell"], "content", [])), "html", null, true);
                    // line 103
                    echo "</";
                    echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute($context["cell"], "tag", [])), "html", null, true);
                    echo ">
          ";
                }
                $_parent = $context['_parent'];
                unset($context['_seq'], $context['_iterated'], $context['_key'], $context['cell'], $context['_parent'], $context['loop']);
                $context = array_intersect_key($context, $_parent) + $_parent;
                // line 105
                echo "        </tr>
      ";
                ++$context['loop']['index0'];
                ++$context['loop']['index'];
                $context['loop']['first'] = false;
                if (isset($context['loop']['length'])) {
                    --$context['loop']['revindex0'];
                    --$context['loop']['revindex'];
                    $context['loop']['last'] = 0 === $context['loop']['revindex0'];
                }
            }
            $_parent = $context['_parent'];
            unset($context['_seq'], $context['_iterated'], $context['_key'], $context['row'], $context['_parent'], $context['loop']);
            $context = array_intersect_key($context, $_parent) + $_parent;
            // line 107
            echo "    </tbody>
  ";
        } elseif (        // line 108
($context["empty"] ?? null)) {
            // line 109
            echo "    <tbody>
      <tr class=\"odd\">
        <td colspan=\"";
            // line 111
            echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed(($context["header_columns"] ?? null)), "html", null, true);
            echo "\" class=\"empty message\">";
            echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed(($context["empty"] ?? null)), "html", null, true);
            echo "</td>
      </tr>
    </tbody>
  ";
        }
        // line 115
        echo "  ";
        if (($context["footer"] ?? null)) {
            // line 116
            echo "    <tfoot>
      ";
            // line 117
            $context['_parent'] = $context;
            $context['_seq'] = twig_ensure_traversable(($context["footer"] ?? null));
            foreach ($context['_seq'] as $context["_key"] => $context["row"]) {
                // line 118
                echo "        <tr";
                echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute($context["row"], "attributes", [])), "html", null, true);
                echo ">
          ";
                // line 119
                $context['_parent'] = $context;
                $context['_seq'] = twig_ensure_traversable($this->getAttribute($context["row"], "cells", []));
                foreach ($context['_seq'] as $context["_key"] => $context["cell"]) {
                    // line 120
                    echo "            <";
                    echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute($context["cell"], "tag", [])), "html", null, true);
                    echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute($context["cell"], "attributes", [])), "html", null, true);
                    echo ">";
                    // line 121
                    echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute($context["cell"], "content", [])), "html", null, true);
                    // line 122
                    echo "</";
                    echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute($context["cell"], "tag", [])), "html", null, true);
                    echo ">
          ";
                }
                $_parent = $context['_parent'];
                unset($context['_seq'], $context['_iterated'], $context['_key'], $context['cell'], $context['_parent'], $context['loop']);
                $context = array_intersect_key($context, $_parent) + $_parent;
                // line 124
                echo "        </tr>
      ";
            }
            $_parent = $context['_parent'];
            unset($context['_seq'], $context['_iterated'], $context['_key'], $context['row'], $context['_parent'], $context['loop']);
            $context = array_intersect_key($context, $_parent) + $_parent;
            // line 126
            echo "    </tfoot>
  ";
        }
        // line 128
        echo "</table>
";
        // line 129
        if (($context["responsive"] ?? null)) {
            // line 130
            echo "  </div>
";
        }
    }

    public function getTemplateName()
    {
        return "themes/contrib/wxt_bootstrap/templates/system/table.html.twig";
    }

    public function isTraitable()
    {
        return false;
    }

    public function getDebugInfo()
    {
        return array (  301 => 130,  299 => 129,  296 => 128,  292 => 126,  285 => 124,  276 => 122,  274 => 121,  269 => 120,  265 => 119,  260 => 118,  256 => 117,  253 => 116,  250 => 115,  241 => 111,  237 => 109,  235 => 108,  232 => 107,  217 => 105,  208 => 103,  206 => 102,  201 => 101,  197 => 100,  192 => 99,  189 => 98,  172 => 97,  169 => 96,  167 => 95,  164 => 94,  159 => 91,  150 => 89,  148 => 88,  143 => 87,  141 => 84,  140 => 83,  138 => 82,  134 => 81,  130 => 79,  128 => 78,  125 => 77,  119 => 76,  113 => 74,  109 => 72,  100 => 70,  96 => 69,  91 => 68,  88 => 67,  84 => 66,  81 => 65,  75 => 63,  73 => 62,  68 => 61,  66 => 58,  65 => 57,  64 => 56,  63 => 55,  62 => 54,  61 => 52,  57 => 49,  55 => 48,);
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
 * Default theme implementation to display a table.
 *
 * Available variables:
 * - attributes: HTML attributes to apply to the <table> tag.
 * - caption: A localized string for the <caption> tag.
 * - colgroups: Column groups. Each group contains the following properties:
 *   - attributes: HTML attributes to apply to the <col> tag.
 *     Note: Drupal currently supports only one table header row, see
 *     https://drupal.org/node/893530 and
 *     http://api.drupal.org/api/drupal/includes!theme.inc/function/theme_table/7#comment-5109.
 * - header: Table header cells. Each cell contains the following properties:
 *   - tag: The HTML tag name to use; either TH or TD.
 *   - attributes: HTML attributes to apply to the tag.
 *   - content: A localized string for the title of the column.
 *   - field: Field name (required for column sorting).
 *   - sort: Default sort order for this column (\"asc\" or \"desc\").
 * - sticky: A flag indicating whether to use a \"sticky\" table header.
 * - rows: Table rows. Each row contains the following properties:
 *   - attributes: HTML attributes to apply to the <tr> tag.
 *   - data: Table cells.
 *   - cells: Table cells of the row. Each cell contains the following keys:
 *     - tag: The HTML tag name to use; either TH or TD.
 *     - attributes: Any HTML attributes, such as \"colspan\", to apply to the
 *       table cell.
 *     - content: The string to display in the table cell.
 *     - active_table_sort: A boolean indicating whether the cell is the active
 *       table sort.
 * - footer: Table footer rows, in the same format as the rows variable.
 * - empty: The message to display in an extra row if table does not have
 *   any rows.
 * - no_striping: A boolean indicating that the row should receive no striping.
 * - header_columns: The number of columns in the header.
 * - bordered: Flag indicating whether or not the table should be bordered.
 * - condensed: Flag indicating whether or not the table should be condensed.
 * - hover: Flag indicating whether or not table rows should be hoverable.
 * - striped: Flag indicating whether or not table rows should be striped.
 * - responsive: Flag indicating whether or not the table should be wrapped to
 *   be responsive (using the Bootstrap Framework .table-responsive wrapper).
 *
 * @ingroup templates
 *
 * @see template_preprocess_table()
 */
#}
{% if responsive %}
  <div class=\"table-responsive\">
{% endif %}
{%
  set classes = [
    'table',
    bordered ? 'table-bordered',
    condensed ? 'table-condensed',
    hover ? 'table-hover',
    striped ? 'table-striped',
    sticky ? 'sticky-enabled',
  ]
%}
<table{{ attributes.addClass(classes) }}>
  {% if caption %}
    <caption>{{ caption }}</caption>
  {% endif %}

  {% for colgroup in colgroups %}
    {% if colgroup.cols %}
      <colgroup{{ colgroup.attributes }}>
        {% for col in colgroup.cols %}
          <col{{ col.attributes }} />
        {% endfor %}
      </colgroup>
    {% else %}
      <colgroup{{ colgroup.attributes }} />
    {% endif %}
  {% endfor %}

  {% if header %}
    <thead>
      <tr>
        {% for cell in header %}
        {%
        set cell_classes = [
          cell.active_table_sort ? 'active',
        ]
        %}
        <{{ cell.tag }}{{ cell.attributes.addClass(cell_classes) }}>
        {{- cell.content -}}
      </{{ cell.tag }}>
      {% endfor %}
      </tr>
    </thead>
  {% endif %}

  {% if rows %}
    <tbody>
      {% for row in rows %}
        {% set row_classes = [cycle(['odd', 'even'], loop.index0)] %}
        <tr{{ row.attributes.addClass(row_classes) }}>
          {% for cell in row.cells %}
            <{{ cell.tag }}{{ cell.attributes }}>
              {{- cell.content -}}
            </{{ cell.tag }}>
          {% endfor %}
        </tr>
      {% endfor %}
    </tbody>
  {% elseif empty %}
    <tbody>
      <tr class=\"odd\">
        <td colspan=\"{{ header_columns }}\" class=\"empty message\">{{ empty }}</td>
      </tr>
    </tbody>
  {% endif %}
  {% if footer %}
    <tfoot>
      {% for row in footer %}
        <tr{{ row.attributes }}>
          {% for cell in row.cells %}
            <{{ cell.tag }}{{ cell.attributes }}>
              {{- cell.content -}}
            </{{ cell.tag }}>
          {% endfor %}
        </tr>
      {% endfor %}
    </tfoot>
  {% endif %}
</table>
{% if responsive %}
  </div>
{% endif %}
", "themes/contrib/wxt_bootstrap/templates/system/table.html.twig", "/var/www/html/tbs-proto1.openplus.ca/themes/contrib/wxt_bootstrap/templates/system/table.html.twig");
    }
}
